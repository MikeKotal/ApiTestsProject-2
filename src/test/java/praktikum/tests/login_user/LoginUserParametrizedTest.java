package praktikum.tests.login_user;

import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.helpers.Initializer;
import praktikum.models.login_user.LoginUserResponse;

import static praktikum.Constants.*;
import static praktikum.clients.ClientsHelper.loginUser;

@RunWith(Parameterized.class)
public class LoginUserParametrizedTest extends Initializer {

    private String email;
    private String password;

    public LoginUserParametrizedTest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Parameterized.Parameters
    public static Object[][] getInputParameters() {
        return new Object[][]{
                {"test", "test"},
                {"", "test"},
                {"test", ""},
                {"", ""},
                {null, "test"}, //дефект, запрос уходит, если не передать поле email
                {"test", null},
                {null, null}
        };
    }

    @Test
    @DisplayName("Check required fields of login")
    public void negativeCheckRequiredFieldsOfLogin() {

        LoginUserResponse loginUserResponse = loginUser(email, password, HttpStatus.SC_UNAUTHORIZED);

        Assert.assertFalse("Нельзя выполнить вход с невалидными параметрами", loginUserResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", LOGIN_USER_REQUIRED_FIELDS, loginUserResponse.getMessage());
    }
}
