package praktikum.tests.loginUser;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.createUser.CreateUserRequest;
import praktikum.models.loginUser.LoginUserRequest;
import praktikum.models.loginUser.LoginUserResponse;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static praktikum.Constants.*;

public class LoginUserTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = STELLAR_BURGERS_URL;
    }

    @Step("Delete test user after creating")
    public void deleteUser(String token) {
        given().auth().oauth2(token).when().delete("/api/auth/user").then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    @Test
    @DisplayName("Check success login created user")
    public void successLoginUser() {
        Random random = new Random();
        String email = "something" + random.nextInt(10000000) + "@yandex.ru";
        String password = "Password" + random.nextInt(10000);
        String name = "Test" + random.nextInt(10000);

        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);

        given()
                .header("Content-type", "application/json")
                .body(createUserRequest)
                .when()
                .post(ENDPOINT_REGISTER);

        LoginUserRequest loginUserRequest =
                new LoginUserRequest(email, password);

        LoginUserResponse loginUserResponse =
                given()
                        .header("Content-type", "application/json")
                        .body(loginUserRequest)
                        .when()
                        .post(ENDPOINT_LOGIN)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body()
                        .as(LoginUserResponse.class);

        deleteUser(loginUserResponse.getAccessToken().split(" ")[1]);

        Assert.assertTrue("Ошибка при логине", loginUserResponse.getSuccess());
        Assert.assertEquals("Проблема с email пользователя",
                createUserRequest.getEmail(), loginUserResponse.getUser().getEmail());
        Assert.assertEquals("Проблема с именем пользователя",
                createUserRequest.getName(), loginUserResponse.getUser().getName());
        Assert.assertNotNull("AccessToken не должен быть пустым", loginUserResponse.getAccessToken());
        Assert.assertNotNull("RefreshToken н должен быть пустым", loginUserResponse.getRefreshToken());
    }
}
