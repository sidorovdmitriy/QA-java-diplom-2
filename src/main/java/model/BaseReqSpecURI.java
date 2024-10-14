package model;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
/**
 * Основная ссылка на Stellar Burgers для пользователя и настройка через getBaseReqSpec
 */
public class BaseReqSpecURI {
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    public RequestSpecification getBaseReqSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URI)
                .build();
    }
}