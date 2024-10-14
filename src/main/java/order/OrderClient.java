package order;

import io.restassured.response.ValidatableResponse;
import model.BaseReqSpecURI;
import model.StellarOrder;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseReqSpecURI {


    public ValidatableResponse orderWithoutAuth(StellarOrder stellarOrder) {
        return given()
                .spec(getBaseReqSpec())
                .body(stellarOrder)
                .post("/api/orders")
                .then();

    }

    public ValidatableResponse orderWithAuth(String accessToken, StellarOrder stellarOrder) {
        return given()
                .spec(getBaseReqSpec())
                .body(stellarOrder)
                .auth().oauth2(accessToken)
                .post("/api/orders")
                .then();
    }

    public ValidatableResponse getOrderUserAuth(String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .auth().oauth2(accessToken)
                .get("/api/orders")
                .then();
    }

    public ValidatableResponse getOrderUserNotAuth() {
        return given()
                .spec(getBaseReqSpec())
                .get("/api/orders")
                .then();
    }
}