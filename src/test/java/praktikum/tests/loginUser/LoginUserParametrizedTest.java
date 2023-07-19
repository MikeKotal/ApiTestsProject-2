package praktikum.tests.loginUser;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.models.loginUser.LoginUserRequest;
import praktikum.models.loginUser.LoginUserResponse;

import static io.restassured.RestAssured.given;
import static praktikum.Constants.*;

@RunWith(Parameterized.class)
public class LoginUserParametrizedTest {

    private LoginUserRequest loginUserRequest;

    public LoginUserParametrizedTest(LoginUserRequest loginUserRequest) {
        this.loginUserRequest = loginUserRequest;
    }

    @Parameterized.Parameters
    public static Object[][] getInputParameters() {
        return new Object[][]{
                {new LoginUserRequest("test", "test")},
                {new LoginUserRequest("", "test")},
                {new LoginUserRequest("test", "")},
                {new LoginUserRequest("", "")},
                {new LoginUserRequest(null, "test")}, //дефект, запрос уходит, если не передать поле email
                {new LoginUserRequest("test", null)},
                {new LoginUserRequest(null, null)}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = STELLAR_BURGERS_URL;
    }

    @Test
    @DisplayName("Check required fields of login")
    public void negativeCheckRequiredFieldsOfLogin() {

        LoginUserResponse loginUserResponse =
                given()
                        .header("Content-type", "application/json")
                        .body(loginUserRequest)
                        .when()
                        .post(ENDPOINT_LOGIN)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_UNAUTHORIZED)
                        .extract()
                        .body()
                        .as(LoginUserResponse.class);

        Assert.assertFalse("Нельзя выполнить вход с невалидными параметрами", loginUserResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", LOGIN_USER_REQUIRED_FIELDS, loginUserResponse.getMessage());
    }
}
