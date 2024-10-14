package user;

import io.restassured.response.ValidatableResponse;
import model.BaseReqSpecURI;
import model.StellarUser;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseReqSpecURI {


    public ValidatableResponse createUser(StellarUser stellarUser) {
        return given()
                .spec(getBaseReqSpec())
                .body(stellarUser)
                .when()
                .post("/api/auth/register")
                .then();
    }

    public ValidatableResponse userLogin(StellarUser stellarUser) {
        return given()
                .spec(getBaseReqSpec())
                .body(stellarUser)
                .when()
                .post("/api/auth/login")
                .then();
    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .auth().oauth2(accessToken)
                .delete("/api/auth/user")
                .then();
    }

    public ValidatableResponse updateUser(String accessToken, StellarUser stellarUser) {
        return given()
                .spec(getBaseReqSpec())
                .body(stellarUser)
                .auth().oauth2(accessToken)
                .patch("/api/auth/user")
                .then();
    }

    public ValidatableResponse updateUserNotAuth(StellarUser stellarUser) {
        return given()
                .spec(getBaseReqSpec())
                .body(stellarUser)
                .patch("/api/auth/user")
                .then();
    }
}