package stellar_burger_api.models.change_user_info;

public class ChangeUserInfoResponse {

    private Boolean success;
    private String message;
    private User user;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
