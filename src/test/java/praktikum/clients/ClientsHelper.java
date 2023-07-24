package praktikum.clients;

import io.qameta.allure.Step;
import org.apache.http.HttpStatus;
import praktikum.models.change_user_info.ChangeUserInfoRequest;
import praktikum.models.change_user_info.ChangeUserInfoResponse;
import praktikum.models.create_order.CreateOrdersRequest;
import praktikum.models.create_order.CreateOrdersResponse;
import praktikum.models.create_user.CreateUserRequest;
import praktikum.models.create_user.CreateUserResponse;
import praktikum.models.get_ingredients.GetIngredientsResponse;
import praktikum.models.get_user_orders.GetUserOrdersResponse;
import praktikum.models.login_user.LoginUserRequest;
import praktikum.models.login_user.LoginUserResponse;

import java.util.ArrayList;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static praktikum.Constants.*;

public class ClientsHelper {

    @Step("Delete test user after creating")
    public static void deleteUser(String token) {
        given().auth().oauth2(token).when().delete(ENDPOINT_USER).then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    @Step("Creating unique test dates for test users")
    public static CreateUserRequest createRandomUser() {
        Random random = new Random();
        String email = "something" + random.nextInt(10000000) + "@yandex.ru";
        String password = "Password" + random.nextInt(10000);
        String name = "Test" + random.nextInt(10000);
        return new CreateUserRequest(email, password, name);
    }

    @Step("Creating test user")
    public static CreateUserResponse createUser(CreateUserRequest createUserRequest, int expectedStatus) {
        return given()
                .header("Content-type", "application/json")
                .body(createUserRequest)
                .when()
                .post(ENDPOINT_REGISTER)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .body()
                .as(CreateUserResponse.class);
    }

    @Step("Login test user")
    public static LoginUserResponse loginUser(String email, String password, int expectedStatus) {
        return given()
                .header("Content-type", "application/json")
                .body(new LoginUserRequest(email, password))
                .when()
                .post(ENDPOINT_LOGIN)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .body()
                .as(LoginUserResponse.class);
    }

    @Step("Create random info for changing")
    public static ChangeUserInfoRequest createRandomInfoForChange() {
        Random random = new Random();
        String newEmail = "something" + random.nextInt(10000000) + "@yandex.ru";
        String newName = "Test" + random.nextInt(10000);
        return new ChangeUserInfoRequest(newEmail, newName);
    }

    @Step("Change info about user test")
    public static ChangeUserInfoResponse changeUserInfo(ChangeUserInfoRequest request, String token, int expectedStatus) {
        return given()
                .auth()
                .oauth2(token)
                .header("Content-type", "application/json")
                .body(request)
                .when()
                .patch(ENDPOINT_USER)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .body()
                .as(ChangeUserInfoResponse.class);
    }

    @Step("Change info without authorization")
    public static ChangeUserInfoResponse changeUserInfoWithoutAuth(ChangeUserInfoRequest request, int expectedStatus) {
        return given()
                .header("Content-type", "application/json")
                .body(request)
                .when()
                .patch(ENDPOINT_USER)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .body()
                .as(ChangeUserInfoResponse.class);
    }

    @Step("Get random ingredients")
    public static ArrayList<String> getIngredients(int countIngredients) {
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
            ingredients.add(getIngredientsResponse.getData()
                    .get(random.nextInt(getIngredientsResponse.getData().size() - 1))
                    .get_id());
        }
        return ingredients;
    }

    @Step("Create random order")
    public static CreateOrdersRequest createRandomOrder(ArrayList<String> ingredients) {
        return new CreateOrdersRequest(ingredients);
    }

    @Step("Creating user orders for checking")
    public static CreateOrdersResponse createOrder(CreateOrdersRequest createOrdersRequest, String token, int expectedStatus) {
        return given()
                .auth()
                .oauth2(token)
                .header("Content-type", "application/json")
                .body(createOrdersRequest)
                .when()
                .post(ENDPOINT_ORDER)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .body()
                .as(CreateOrdersResponse.class);
    }

    @Step("Creating user orders for checking without authorization")
    public static CreateOrdersResponse createOrderWithoutAuth(CreateOrdersRequest createOrdersRequest, int expectedStatus) {
        return given()
                .header("Content-type", "application/json")
                .body(createOrdersRequest)
                .when()
                .post(ENDPOINT_ORDER)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .body()
                .as(CreateOrdersResponse.class);
    }

    @Step("Get test user orders with auth")
    public static GetUserOrdersResponse userOrders(String token, int expectedStatus) {
        return given()
                .auth()
                .oauth2(token)
                .when()
                .get(ENDPOINT_ORDER)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .body()
                .as(GetUserOrdersResponse.class);
    }

    @Step("Get test user orders without authorization")
    public static GetUserOrdersResponse userOrdersWithoutAuth(int expectedStatus) {
        return given()
                .when()
                .get(ENDPOINT_ORDER)
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .extract()
                .body()
                .as(GetUserOrdersResponse.class);
    }

}
