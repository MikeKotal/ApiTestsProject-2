package stellar_burger_api.models.create_order;

public class CreateOrdersResponse {

    private Boolean success;
    private String message;
    private String name;
    private Order order;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public Order getOrder() {
        return order;
    }
}
