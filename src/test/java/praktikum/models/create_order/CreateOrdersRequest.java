package praktikum.models.create_order;

import java.util.ArrayList;

public class CreateOrdersRequest {

    private ArrayList<String> ingredients;

    public CreateOrdersRequest(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}
