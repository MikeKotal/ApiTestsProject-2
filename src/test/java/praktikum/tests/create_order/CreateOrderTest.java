package praktikum.tests.create_order;

import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import praktikum.helpers.Initializer;
import praktikum.models.create_order.CreateOrdersRequest;
import praktikum.models.create_order.CreateOrdersResponse;
import praktikum.models.create_user.CreateUserResponse;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static praktikum.Constants.*;
import static praktikum.clients.ClientsHelper.*;

public class CreateOrderTest extends Initializer {

    @Test
    @DisplayName("Check success creating order with authorization")
    public void successCreateOrderWithAuth() {
        CreateUserResponse user = createUser(createRandomUser(), HttpStatus.SC_OK);
        String token = user.getAccessToken().split(" ")[1];

        CreateOrdersRequest createOrdersRequest = createRandomOrder(getIngredients(2));
        CreateOrdersResponse createOrdersResponse = createOrder(createOrdersRequest, token, HttpStatus.SC_OK);
        deleteUser(token);

        Assert.assertTrue("Произошла ошибка при создании заказа", createOrdersResponse.getSuccess());
        Assert.assertNotNull("Наименование бургера должно быть заполнено", createOrdersResponse.getName());
        Assert.assertEquals("Некорректное количество ингредиентов",
                2, createOrdersResponse.getOrder().getIngredients().size());
        Assert.assertEquals("Имя пользователя отличается",
                user.getUser().getName(), createOrdersResponse.getOrder().getOwner().getName());
        Assert.assertEquals("Email пользователя отличается",
                user.getUser().getEmail(), createOrdersResponse.getOrder().getOwner().getEmail());
        Assert.assertNotNull("Дата создания пользователя не должно быть пустым",
                createOrdersResponse.getOrder().getOwner().getCreatedAt());
        Assert.assertNotNull("Дата обновления пользователя не должно быть пустым",
                createOrdersResponse.getOrder().getOwner().getUpdatedAt());
        Assert.assertNotNull("Полу статус должно быть заполнено", createOrdersResponse.getOrder().getStatus());
        Assert.assertNotNull("Наименование бургера должно быть заполнено",
                createOrdersResponse.getOrder().getName());
        Assert.assertNotNull("Дата создания заказа не должно быть пустым",
                createOrdersResponse.getOrder().getCreatedAt());
        Assert.assertNotNull("Дата обновления заказа не должно быть пустым",
                createOrdersResponse.getOrder().getUpdatedAt());
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
        CreateOrdersResponse createOrdersResponse =
                createOrderWithoutAuth(createRandomOrder(getIngredients(2)), HttpStatus.SC_OK);

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
        CreateOrdersResponse createOrdersResponse =
                createOrderWithoutAuth(createRandomOrder(null), HttpStatus.SC_BAD_REQUEST);

        Assert.assertFalse("Нельзя создать заказ без ингредиентов", createOrdersResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", INGREDIENTS_REQUIRED_ERROR, createOrdersResponse.getMessage());
    }

    @Test
    @DisplayName("Check creating order with invalid ingredients")
    public void negativeCreateOrderWithInvalidIngredients() {
        CreateUserResponse user = createUser(createRandomUser(), HttpStatus.SC_OK);
        String token = user.getAccessToken().split(" ")[1];
        given()
                .auth()
                .oauth2(token)
                .header("Content-type", "application/json")
                .body(createRandomOrder(new ArrayList<>(List.of("Test"))))
                .when()
                .post(ENDPOINT_ORDER)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        deleteUser(token);
    }

}
