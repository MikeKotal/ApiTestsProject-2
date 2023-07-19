package praktikum.models.getUserOrders;

import java.util.ArrayList;

public class GetUserOrdersResponse {

    private Boolean success;
    private String message;
    private ArrayList<Orders> orders;
    private Integer total;
    private Integer totalToday;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Orders> getOrders() {
        return orders;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getTotalToday() {
        return totalToday;
    }
}
