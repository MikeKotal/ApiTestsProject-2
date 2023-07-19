package praktikum.tests.createOrders;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.createOrders.CreateOrdersRequest;
import praktikum.models.createOrders.CreateOrdersResponse;
import praktikum.models.createUser.CreateUserRequest;
import praktikum.models.createUser.CreateUserResponse;
import praktikum.models.getIngredients.GetIngredientsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static praktikum.Constants.*;

public class CreateOrderTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = STELLAR_BURGERS_URL;
    }

    @Step("Delete test user after creating")
    public void deleteUser(String token) {
        given().auth().oauth2(token).when().delete("/api/auth/user").then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    @Step("Creating user for create order")
    public CreateUserResponse createUser() {
        Random random = new Random();
        String email = "something" + random.nextInt(10000000) + "@yandex.ru";
        String password = "Password" + random.nextInt(10000);
        String name = "Test" + random.nextInt(10000);

        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);

        return given()
                .header("Content-type", "application/json")
                .body(createUserRequest)
                .when()
                .post(ENDPOINT_REGISTER)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .as(CreateUserResponse.class);
    }

    @Step("Get random ingredients")
    public ArrayList<String> getIngredients(int countIngredients) {
        Random random = new Random();
        ArrayList<String> ingredients = new ArrayList<>();
        GetIngredientsResponse getIngredientsResponse =
                given()
                        .when()
                        .get(ENDPOINT_INGREDIENTS)
                        .then()
                        .extract()
                        .body()
                        .as(GetIngredientsResponse.class);

        for (int i = 0; i < countIngredients; i++) {
            ingredients.add(getIngredientsResponse.getData().get(i).get_id());
        }
        return ingredients;
    }

    @Test
    @DisplayName("Check success creating order with authorization")
    public void successCreateOrderWithAuth() {
        CreateUserResponse user = createUser();
        String token = user.getAccessToken().split(" ")[1];

        CreateOrdersRequest createOrdersRequest = new CreateOrdersRequest(getIngredients(2));

        CreateOrdersResponse createOrdersResponse =
                given()
                        .auth()
                        .oauth2(token)
                        .header("Content-type", "application/json")
                        .body(createOrdersRequest)
                        .when()
                        .post(ENDPOINT_ORDER)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body()
                        .as(CreateOrdersResponse.class);

        deleteUser(token);

        Assert.assertTrue("Произошла ошибка при создании заказа", createOrdersResponse.getSuccess());
        Assert.assertNotNull("Наименование бургера должно быть заполнено", createOrdersResponse.getName());
        Assert.assertEquals("Некорректное количество ингредиентов",
                2, createOrdersResponse.getOrder().getIngredients().size());
        Assert.assertEquals("Имя пользователя отличается",
                user.getUser().getName(), createOrdersResponse.getOrder().getOwner().getName());
        Assert.assertEquals("Email пользователя отличается",
                user.getUser().getEmail(), createOrdersResponse.getOrder().getOwner().getEmail());
        Assert.assertNotNull("Полу статус должно быть заполнено", createOrdersResponse.getOrder().getStatus());
        Assert.assertNotNull("Наименование бургера должно быть заполнено",
                createOrdersResponse.getOrder().getName());
        Assert.assertNotNull("Номер заказа не должен быть пустым", createOrdersResponse.getOrder().getNumber());
        Assert.assertNotNull("Поле цены должно быть заполнено", createOrdersResponse.getOrder().getPrice());

        for (int i = 0; i < 2; i++) {
            Assert.assertTrue("Вернулся неизвестный ингредиент",
                    createOrdersRequest.getIngredients().contains(createOrdersResponse.getOrder().getIngredients().get(i).get_id()));
        }
    }

    @Test
    @DisplayName("Check success creating order without authorization")
    public void successCreateOrderWithoutAuth() {

        CreateOrdersRequest createOrdersRequest = new CreateOrdersRequest(getIngredients(2));

        CreateOrdersResponse createOrdersResponse =
                given()
                        .header("Content-type", "application/json")
                        .body(createOrdersRequest)
                        .when()
                        .post(ENDPOINT_ORDER)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body()
                        .as(CreateOrdersResponse.class);

        Assert.assertTrue("Произошла ошибка при создании заказа", createOrdersResponse.getSuccess());
        Assert.assertNotNull("Наименование бургера должно быть заполнено", createOrdersResponse.getName());
        Assert.assertNull("Пользователь неавторизован, массив не заполняется",
                createOrdersResponse.getOrder().getIngredients());
        Assert.assertNull("Пользователь неавторизован, объект с данными пользователя должен быть пустым",
                createOrdersResponse.getOrder().getOwner());
        Assert.assertNull("Пользователь неавторизован, поле статус должно быть пустым",
                createOrdersResponse.getOrder().getStatus());
        Assert.assertNull("Пользователь неавторизован, поле имя должно быть пустым",
                createOrdersResponse.getOrder().getName());
        Assert.assertNotNull("Номер заказа не должен быть пустым", createOrdersResponse.getOrder().getNumber());
        Assert.assertNull("Пользователь неавторизован, поле цена должно быть пустым",
                createOrdersResponse.getOrder().getPrice());
    }

    @Test
    @DisplayName("Check creating order without ingredients")
    public void negativeCreateOrderWithoutIngredients() {

        CreateOrdersRequest createOrdersRequest = new CreateOrdersRequest(null);

        CreateOrdersResponse createOrdersResponse =
                given()
                        .header("Content-type", "application/json")
                        .body(createOrdersRequest)
                        .when()
                        .post(ENDPOINT_ORDER)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .extract()
                        .body()
                        .as(CreateOrdersResponse.class);

        Assert.assertFalse("Нельзя создать заказ без ингредиентов", createOrdersResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", INGREDIENTS_REQUIRED_ERROR, createOrdersResponse.getMessage());
    }

    @Test
    @DisplayName("Check creating order with invalid ingredients")
    public void negativeCreateOrderWithInvalidIngredients() {
        CreateUserResponse user = createUser();
        String token = user.getAccessToken().split(" ")[1];

        CreateOrdersRequest createOrdersRequest = new CreateOrdersRequest(new ArrayList<>(List.of("Test")));

        given()
                .auth()
                .oauth2(token)
                .header("Content-type", "application/json")
                .body(createOrdersRequest)
                .when()
                .post(ENDPOINT_ORDER)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        deleteUser(token);
    }

}
