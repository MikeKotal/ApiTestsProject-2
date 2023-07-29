package praktikum;

public interface Constants {

    String STELLAR_BURGERS_URL = "https://stellarburgers.nomoreparties.site";
    String USER_EXISTS = "User already exists";
    String CREATE_USER_REQUIRED_FIELDS = "Email, password and name are required fields";
    String LOGIN_USER_REQUIRED_FIELDS = "email or password are incorrect";
    String AUTH_ERROR_INFO = "You should be authorised";
    String SIMILAR_EMAIL_ERROR = "User with such email already exists";
    String INGREDIENTS_REQUIRED_ERROR = "Ingredient ids must be provided";
    String ENDPOINT_REGISTER = "/api/auth/register";
    String ENDPOINT_LOGIN = "/api/auth/login";
    String ENDPOINT_USER = "/api/auth/user";
    String ENDPOINT_INGREDIENTS = "/api/ingredients";
    String ENDPOINT_ORDER = "/api/orders";

}
