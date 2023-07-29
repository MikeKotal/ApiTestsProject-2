package stellar_burger_api.models.get_user_orders;

import java.util.ArrayList;

public class GetUserOrdersResponse {

    private Boolean success;
    private String message;
    private ArrayList<Order> orders;
    private Integer total;
    private Integer totalToday;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getTotalToday() {
        return totalToday;
    }
}
