package praktikum.tests.login_user;

import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import praktikum.helpers.Initializer;
import praktikum.models.create_user.CreateUserRequest;
import praktikum.models.login_user.LoginUserResponse;

import static praktikum.clients.ClientsHelper.*;

public class LoginUserTest extends Initializer {

    @Test
    @DisplayName("Check success login created user")
    public void successLoginUser() {

        CreateUserRequest createUserRequest = createRandomUser();
        createUser(createUserRequest, HttpStatus.SC_OK);
        LoginUserResponse loginUserResponse =
                loginUser(createUserRequest.getEmail(), createUserRequest.getPassword(), HttpStatus.SC_OK);
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
