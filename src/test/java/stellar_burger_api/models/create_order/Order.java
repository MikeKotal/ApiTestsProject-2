package stellar_burger_api.models.create_order;

import java.util.ArrayList;

public class Order {

    private ArrayList<Ingredients> ingredients;
    private String _id;
    private Owner owner;
    private String status;
    private String name;
    private String createdAt;
    private String updatedAt;
    private Integer number;
    private String price;

    public ArrayList<Ingredients> getIngredients() {
        return ingredients;
    }

    public String get_id() {
        return _id;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Integer getNumber() {
        return number;
    }

    public String getPrice() {
        return price;
    }
}
