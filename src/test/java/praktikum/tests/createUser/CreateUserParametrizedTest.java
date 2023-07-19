package praktikum.tests.createUser;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.models.createUser.CreateUserRequest;
import praktikum.models.createUser.CreateUserResponse;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static praktikum.Constants.*;

@RunWith(Parameterized.class)
public class CreateUserParametrizedTest {

    private CreateUserRequest createUserRequest;

    public CreateUserParametrizedTest(CreateUserRequest createUserRequest) {
        this.createUserRequest = createUserRequest;
    }

    @Parameterized.Parameters
    public static Object[][] getInputParameters() {
        Random random = new Random();
        String email = "something" + random.nextInt(10000000) + "@yandex.ru";
        String password = "Password" + random.nextInt(10000);
        String name = "Test" + random.nextInt(10000);
        return new Object[][]{
                {new CreateUserRequest(null, password, name)},
                {new CreateUserRequest(email, null, name)},
                {new CreateUserRequest(email, password, null)},
                {new CreateUserRequest(null, null, null)},
                {new CreateUserRequest("", password, name)},
                {new CreateUserRequest(email, "", name)},
                {new CreateUserRequest(email, password, "")},
                {new CreateUserRequest("", "", "")}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = STELLAR_BURGERS_URL;
    }

    @Test
    @DisplayName("Check required fields of register")
    public void negativeCheckRequiredFieldsOfRegister() {

        CreateUserResponse createUserResponse =
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

        Assert.assertFalse("Нельзя создавать пользователя с пустым полем", createUserResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", CREATE_USER_REQUIRED_FIELDS, createUserResponse.getMessage());
    }

}
