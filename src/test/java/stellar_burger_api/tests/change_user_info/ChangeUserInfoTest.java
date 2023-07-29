package stellar_burger_api.tests.change_user_info;

import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import stellar_burger_api.helpers.Initializer;
import stellar_burger_api.models.change_user_info.ChangeUserInfoRequest;
import stellar_burger_api.models.change_user_info.ChangeUserInfoResponse;
import stellar_burger_api.models.create_user.CreateUserResponse;

import java.util.Random;

import static stellar_burger_api.Constants.*;
import static stellar_burger_api.clients.ClientsHelper.*;

public class ChangeUserInfoTest extends Initializer {

    @Test
    @DisplayName("Check success changing user info")
    public void successChangeUserInfo() {

        CreateUserResponse createUserResponse = createUser(createRandomUser(), HttpStatus.SC_OK);
        String token = createUserResponse.getAccessToken().split(" ")[1];
        ChangeUserInfoRequest changeUserInfoRequest = createRandomInfoForChange();
        ChangeUserInfoResponse changeUserInfoResponse = changeUserInfo(changeUserInfoRequest, token, HttpStatus.SC_OK);
        deleteUser(token);

        Assert.assertTrue("Произошла ошибка при изменении параметров", changeUserInfoResponse.getSuccess());
        Assert.assertEquals("Ошибка при изменении email", changeUserInfoRequest.getEmail(),
                changeUserInfoResponse.getUser().getEmail());
        Assert.assertEquals("Ошибка при изменении имени", changeUserInfoRequest.getName(),
                changeUserInfoResponse.getUser().getName());
    }

    @Test
    @DisplayName("Check changing user info without authorization")
    public void negativeChangeUserInfoWithoutAuth() {

        ChangeUserInfoResponse changeUserInfoResponse =
                changeUserInfoWithoutAuth(createRandomInfoForChange(), HttpStatus.SC_UNAUTHORIZED);

        Assert.assertFalse("Нельзя изменить информацию без авторизации", changeUserInfoResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", AUTH_ERROR_INFO, changeUserInfoResponse.getMessage());
    }

    @Test
    @DisplayName("Check changing email to exists email")
    public void negativeChangeEmailToExistEmail() {

        CreateUserResponse firstUser = createUser(createRandomUser(), HttpStatus.SC_OK);
        CreateUserResponse secondUser = createUser(createRandomUser(), HttpStatus.SC_OK);
        String firstToken = firstUser.getAccessToken().split(" ")[1];
        String secondToken = secondUser.getAccessToken().split(" ")[1];

        Random random = new Random();
        String existsEmail = firstUser.getUser().getEmail();
        String newName = "Test" + random.nextInt(10000);

        ChangeUserInfoResponse changeUserInfoResponse =
                changeUserInfo(new ChangeUserInfoRequest(existsEmail, newName), secondToken, HttpStatus.SC_FORBIDDEN);

        deleteUser(firstToken);
        deleteUser(secondToken);

        Assert.assertFalse("Нельзя менять email на существующий", changeUserInfoResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", SIMILAR_EMAIL_ERROR, changeUserInfoResponse.getMessage());
    }
}
