package model;

import io.restassured.response.Response;
import order.OrderClient;

public class GetIngredientId  {
    private OrderClient orderClient;
    public String name;

    public String GetIngredientId(String name){
        this.name = name;
        return name;
    }

    public String getid(String name) {
        orderClient = new OrderClient();
        this.name = name;
        Response response = orderClient.getOrderIngredientId();
        return response.jsonPath().getString("data.find {it.name = '+ name +'}._id");
    }


}
