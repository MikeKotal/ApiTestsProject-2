package praktikum.tests.create_user;

import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.helpers.Initializer;
import praktikum.models.create_user.CreateUserRequest;
import praktikum.models.create_user.CreateUserResponse;

import java.util.Random;

import static praktikum.Constants.*;
import static praktikum.clients.ClientsHelper.createUser;

@RunWith(Parameterized.class)
public class CreateUserParametrizedTest extends Initializer {

    private String email;
    private String password;
    private String name;

    public CreateUserParametrizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] getInputParameters() {
        Random random = new Random();
        String email = "something" + random.nextInt(10000000) + "@yandex.ru";
        String password = "Password" + random.nextInt(10000);
        String name = "Test" + random.nextInt(10000);
        return new Object[][]{
                {null, password, name},
                {email, null, name},
                {email, password, null},
                {null, null, null},
                {"", password, name},
                {email, "", name},
                {email, password, ""},
                {"", "", ""}
        };
    }

    @Test
    @DisplayName("Check required fields of register")
    public void negativeCheckRequiredFieldsOfRegister() {

        CreateUserResponse createUserResponse =
                createUser(new CreateUserRequest(email, password, name), HttpStatus.SC_FORBIDDEN);

        Assert.assertFalse("Нельзя создавать пользователя с пустым полем", createUserResponse.getSuccess());
        Assert.assertEquals("Некорректная ошибка", CREATE_USER_REQUIRED_FIELDS, createUserResponse.getMessage());
    }

}
