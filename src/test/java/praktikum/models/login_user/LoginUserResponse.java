package praktikum.models.login_user;

public class LoginUserResponse {

    private Boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private User user;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }
}
