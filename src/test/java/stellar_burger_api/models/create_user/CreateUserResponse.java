package stellar_burger_api.models.create_user;

public class CreateUserResponse {

    private Boolean success;
    private String message;
    private User user;
    private String accessToken;
    private String refreshToken;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
