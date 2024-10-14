package model;

import java.util.ArrayList;
/**
 * Содержит массив ингредиентов
 */
public class StellarOrder {
    private ArrayList<String> ingredients;

    public StellarOrder(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}