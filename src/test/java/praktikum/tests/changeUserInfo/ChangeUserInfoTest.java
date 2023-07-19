package praktikum.tests.changeUserInfo;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.changeUserInfo.ChangeUserInfoRequest;
import praktikum.models.changeUserInfo.ChangeUserInfoResponse;
import praktikum.models.createUser.CreateUserRequest;
import praktikum.models.createUser.CreateUserResponse;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static praktikum.Constants.*;

public class ChangeUserInfoTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = STELLAR_BURGERS_URL;
    }

    @Step("Delete test user after creating")
    public void deleteUser(String token) {
        given().auth().oauth2(token).when().delete("/api/auth/user").then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    @Step("Creating user for change info")
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

    @Test
    @DisplayName("Check success changing user info")
    public void successChangeUserInfo() {

        CreateUserResponse user = createUser();
        String token = user.getAccessToken().split(" ")[1];

        Random random = new Random();
        String newEmail = "something" + random.nextInt(10000000) + "@yandex.ru";
        String newName = "Test" + random.nextInt(10000);

        ChangeUserInfoRequest changeUserInfoRequest = new ChangeUserInfoRequest(newEmail, newName);

        ChangeUserInfoResponse changeUserInfoResponse =
                given()
                        .auth()
                        .oauth2(token)
                        .header("Content-type", "application/json")
                        .body(changeUserInfoRequest)
                        .when()
                        .patch(ENDPOINT_USER)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body()
                        .as(ChangeUserInfoResponse.class);

        deleteUser(token);

        Assert.assertTrue("Произошла ошибка при изменении параметров", changeUserInfoResponse.getSuccess());
        Assert.assertEquals("Ошибка при изменении email", newEmail, changeUserInfoResponse.getUser().getEmail());
        Assert.assertEquals("Ошибка при изменении имени", newName, changeUserInfoResponse.getUser().getName());
    }

    @Test
    @DisplayName("Check changing user info without authorization")
    public void negativeChangeUserInfoWithoutAuth() {
        Random random = new Random();
        String newEmail = "something" + random.nextInt(10000000) + "@yandex.ru";
        String newName = "Test" + random.nextInt(10000);

        ChangeUserInfoRequest changeUserInfoRequest = new ChangeUserInfoRequest(newEmail, newName);

        ChangeUserInfoResponse changeUserInfoResponse =
                given()
                        .header("Content-type", "application/json")
                        .body(changeUserInfoRequest)
                        .when()
                        .patch(ENDPOINT_USER)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_UNAUTHORIZED)
                        .extract()
                        .body()
                        .as(ChangeUserInfoResponse.class);

        Assert.assertFalse("Нельзя изменить информацию без авторизации", changeUserInfoResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", AUTH_ERROR_INFO, changeUserInfoResponse.getMessage());
    }

    @Test
    @DisplayName("Check changing email to exists email")
    public void negativeChangeEmailToExistEmail() {

        CreateUserResponse firstUser = createUser();
        CreateUserResponse secondUser = createUser();
        String firstToken = firstUser.getAccessToken().split(" ")[1];
        String secondToken = secondUser.getAccessToken().split(" ")[1];

        Random random = new Random();
        String existsEmail = firstUser.getUser().getEmail();
        String newName = "Test" + random.nextInt(10000);

        ChangeUserInfoRequest changeUserInfoRequest = new ChangeUserInfoRequest(existsEmail, newName);

        ChangeUserInfoResponse changeUserInfoResponse =
                given()
                        .auth()
                        .oauth2(secondToken)
                        .header("Content-type", "application/json")
                        .body(changeUserInfoRequest)
                        .when()
                        .patch(ENDPOINT_USER)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_FORBIDDEN)
                        .extract()
                        .body()
                        .as(ChangeUserInfoResponse.class);

        deleteUser(firstToken);
        deleteUser(secondToken);

        Assert.assertFalse("Нельзя менять email на существующий", changeUserInfoResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", SIMILAR_EMAIL_ERROR, changeUserInfoResponse.getMessage());
    }

}
