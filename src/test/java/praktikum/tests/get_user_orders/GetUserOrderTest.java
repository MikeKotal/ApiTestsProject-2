package praktikum.tests.get_user_orders;

import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import praktikum.helpers.Initializer;
import praktikum.models.create_order.CreateOrdersRequest;
import praktikum.models.create_order.CreateOrdersResponse;
import praktikum.models.create_user.CreateUserResponse;
import praktikum.models.get_user_orders.GetUserOrdersResponse;

import static praktikum.Constants.*;
import static praktikum.clients.ClientsHelper.*;

public class GetUserOrderTest extends Initializer {

    @Test
    @DisplayName("Success check user orders with authorization")
    public void successGetUserOrders() {
        CreateUserResponse user = createUser(createRandomUser(), HttpStatus.SC_OK);
        String token = user.getAccessToken().split(" ")[1];
        CreateOrdersRequest createOrdersRequest = createRandomOrder(getIngredients(2));
        CreateOrdersResponse order = createOrder(createOrdersRequest, token, HttpStatus.SC_OK);
        GetUserOrdersResponse getUserOrdersResponse = userOrders(token, HttpStatus.SC_OK);
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

    @Test
    @DisplayName("Check a couple of orders and sort by updatedAt")
    public void checkCoupleOrders() {
        CreateUserResponse user = createUser(createRandomUser(), HttpStatus.SC_OK);
        String token = user.getAccessToken().split(" ")[1];
        createOrder(new CreateOrdersRequest(getIngredients(2)), token, HttpStatus.SC_OK);
        CreateOrdersResponse secondOrder =
                createOrder(new CreateOrdersRequest(getIngredients(2)), token, HttpStatus.SC_OK);
        GetUserOrdersResponse getUserOrdersResponse = userOrders(token, HttpStatus.SC_OK);
        deleteUser(token);

        Assert.assertEquals("Неверное количество заказов", 2, getUserOrdersResponse.getOrders().size());
        Assert.assertEquals("Неверная сортировка по времени обновления заказа",
                secondOrder.getOrder().getUpdatedAt(), getUserOrdersResponse.getOrders().get(1).getUpdatedAt());
        Assert.assertEquals("Некорректный идентификатор заказа",
                secondOrder.getOrder().get_id(), getUserOrdersResponse.getOrders().get(1).get_id());
    }

    @Test
    @DisplayName("Check orders without authorization")
    public void negativeGetUserOrdersWithoutAuth() {
        GetUserOrdersResponse getUserOrdersResponse = userOrdersWithoutAuth(HttpStatus.SC_UNAUTHORIZED);

        Assert.assertFalse("Нельзя получить инфо без авторизации", getUserOrdersResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", AUTH_ERROR_INFO, getUserOrdersResponse.getMessage());
    }
}
