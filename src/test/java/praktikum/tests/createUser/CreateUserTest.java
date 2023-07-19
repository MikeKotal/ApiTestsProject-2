package praktikum.tests.createUser;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.createUser.CreateUserRequest;
import praktikum.models.createUser.CreateUserResponse;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static praktikum.Constants.*;

public class CreateUserTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = STELLAR_BURGERS_URL;
    }

    @Step("Delete test user after creating")
    public void deleteUser(String token) {
        given().auth().oauth2(token).when().delete("/api/auth/user").then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    @Step("Creating unique test dates")
    public CreateUserRequest creatorDates() {
        Random random = new Random();
        String email = "something" + random.nextInt(10000000) + "@yandex.ru";
        String password = "Password" + random.nextInt(10000);
        String name = "Test" + random.nextInt(10000);
        return new CreateUserRequest(email, password, name);
    }

    @Test
    @DisplayName("Check creating new unique user")
    public void successCreateUser() {

        CreateUserRequest createUserRequest = creatorDates();

        CreateUserResponse createUserResponse =
                given()
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

        deleteUser(createUserResponse.getAccessToken().split(" ")[1]);

        Assert.assertTrue("Ошибка при создании пользователя", createUserResponse.getSuccess());
        Assert.assertEquals("Некорректный email при создании пользователя",
                createUserRequest.getEmail(), createUserResponse.getUser().getEmail());
        Assert.assertEquals("Некорректное имя при создании пользователя",
                createUserRequest.getName(), createUserResponse.getUser().getName());
        Assert.assertNotNull("AccessToken не должен быть пустым", createUserResponse.getAccessToken());
        Assert.assertNotNull("RefreshToken не должен быть пустым", createUserResponse.getRefreshToken());
    }

    @Test
    @DisplayName("Check creating two similar users")
    public void negativeCreateSimilarUsers() {

        CreateUserRequest createUserRequest = creatorDates();

        CreateUserResponse createUserResponse =
        given()
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

        String token = createUserResponse.getAccessToken().split(" ")[1];

        createUserResponse =
                given()
                        .header("Content-type", "application/json")
                        .body(createUserRequest)
                        .when()
                        .post(ENDPOINT_REGISTER)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_FORBIDDEN)
                        .extract()
                        .body()
                        .as(CreateUserResponse.class);

        deleteUser(token);

        Assert.assertFalse("Нельзя создать 2-х одинаковых пользователей", createUserResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", USER_EXISTS, createUserResponse.getMessage());
    }
}
