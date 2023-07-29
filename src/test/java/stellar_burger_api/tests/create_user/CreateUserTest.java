package stellar_burger_api.tests.create_user;

import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import stellar_burger_api.helpers.Initializer;
import stellar_burger_api.models.create_user.CreateUserRequest;
import stellar_burger_api.models.create_user.CreateUserResponse;

import static stellar_burger_api.Constants.*;
import static stellar_burger_api.clients.ClientsHelper.*;

public class CreateUserTest extends Initializer {

    @Test
    @DisplayName("Check creating new unique user")
    public void successCreateUser() {

        CreateUserRequest createUserRequest = createRandomUser();
        CreateUserResponse createUserResponse = createUser(createUserRequest, HttpStatus.SC_OK);

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

        CreateUserRequest createUserRequest = createRandomUser();
        CreateUserResponse createUserResponse = createUser(createUserRequest, HttpStatus.SC_OK);
        String token = createUserResponse.getAccessToken().split(" ")[1];
        createUserResponse = createUser(createUserRequest, HttpStatus.SC_FORBIDDEN);
        deleteUser(token);

        Assert.assertFalse("Нельзя создать 2-х одинаковых пользователей", createUserResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", USER_EXISTS, createUserResponse.getMessage());
    }
}
