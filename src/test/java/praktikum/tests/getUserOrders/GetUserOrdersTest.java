package praktikum.tests.getUserOrders;

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
import praktikum.models.getUserOrders.GetUserOrdersResponse;

import java.util.ArrayList;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static praktikum.Constants.*;

public class GetUserOrdersTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = STELLAR_BURGERS_URL;
    }

    @Step("Delete test user after creating")
    public void deleteUser(String token) {
        given().auth().oauth2(token).when().delete("/api/auth/user").then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    @Step("Creating user for create and get orders")
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
            ingredients.add(getIngredientsResponse
                    .getData()
                    .get(random.nextInt(getIngredientsResponse.getData().size()))
                    .get_id());
        }
        return ingredients;
    }

    @Step("Creating user orders for checking")
    public CreateOrdersResponse createOrder(CreateOrdersRequest createOrdersRequest, String token) {

        return given()
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
    }

    @Test
    @DisplayName("Success check user orders with authorization")
    public void successGetUserOrders() {
        CreateUserResponse user = createUser();
        String token = user.getAccessToken().split(" ")[1];
        CreateOrdersRequest createOrdersRequest = new CreateOrdersRequest(getIngredients(2));
        CreateOrdersResponse order = createOrder(createOrdersRequest, token);

        GetUserOrdersResponse getUserOrdersResponse =
                given()
                        .auth()
                        .oauth2(token)
                        .when()
                        .get(ENDPOINT_ORDER)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body()
                        .as(GetUserOrdersResponse.class);

        deleteUser(token);

        Assert.assertTrue("Ошибка при получении списка заказов", getUserOrdersResponse.getSuccess());
        Assert.assertEquals("Количество возвращенных заказов отличается от созданных",
                1, getUserOrdersResponse.getOrders().size());
        Assert.assertEquals("Ингредиенты не совпадают",
                createOrdersRequest.getIngredients(), getUserOrdersResponse.getOrders().get(0).getIngredients());
        Assert.assertEquals("Несоответствие идентификатора заказа",
                order.getOrder().get_id(), getUserOrdersResponse.getOrders().get(0).get_id());
        Assert.assertEquals("Несоответствие статуса заказа",
                order.getOrder().getStatus(), getUserOrdersResponse.getOrders().get(0).getStatus());
        Assert.assertEquals("Несоответствует название бургера",
                order.getOrder().getName(), getUserOrdersResponse.getOrders().get(0).getName());
        Assert.assertEquals("Несоответствует дата заказа",
                order.getOrder().getCreatedAt(), getUserOrdersResponse.getOrders().get(0).getCreatedAt());
        Assert.assertEquals("Несоответствует дата обновления заказа",
                order.getOrder().getUpdatedAt(), getUserOrdersResponse.getOrders().get(0).getUpdatedAt());
        Assert.assertEquals("Несоответствует номер заказа",
                order.getOrder().getNumber(), getUserOrdersResponse.getOrders().get(0).getNumber());
    }
}
