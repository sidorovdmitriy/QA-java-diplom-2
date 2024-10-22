import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.StellarUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testValue.TestValue;
import user.UserClient;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 3. Изменение данных пользователя:
 */
public class ChangeUserDataTest {
    private UserClient userClient;

    @Before
    @Step("Создание пользователя")
    public void setUp() {
        userClient = new UserClient();
        StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        userClient.createUser(StellarUser);
    }

    @After
    @Step("Удаление пользователя после прохождения теста")
    public void clearData() {
        try {
            StellarUser StellarUserTwo = new StellarUser(TestValue.LOGIN_TWO_TEST, TestValue.PASSWORD_TWO_TEST, TestValue.TEST_NAME_TWO);
            ValidatableResponse loginResponse = userClient.userLogin(StellarUserTwo);
            String accessTokenWithBearer = loginResponse.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("удален");
        } catch (Exception e) {
            System.out.println("Пользователь не удалился");
        }
    }

    @Test
    @DisplayName("Изменение информации о пользвателе с авторизацией")
    @Description("Patch-запрос  /api/auth/user")
    @Step("Изменение информации")
    public void updateUserWithAuth() {
        StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        ValidatableResponse loginResponse = userClient.userLogin(StellarUser);
        String accessTokenWithBearer = loginResponse.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        StellarUser StellarUserTwo = new StellarUser(TestValue.LOGIN_TWO_TEST, TestValue.PASSWORD_TWO_TEST, TestValue.TEST_NAME_TWO);
        ValidatableResponse responseUpdate = userClient.updateUser(accessToken, StellarUserTwo);
        responseUpdate.assertThat().statusCode(HTTP_OK);
        responseUpdate.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение информации о пользвателе без авторизации")
    @Description("Patch-запрос /api/auth/user")
    @Step("Изменение информации")
    public void updateUserWithoutAuth() {
        StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        ValidatableResponse loginResponse = userClient.userLogin(StellarUser);
        String accessTokenWithBearer = loginResponse.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        StellarUser StellarUserTwo = new StellarUser(TestValue.LOGIN_TWO_TEST, TestValue.PASSWORD_TWO_TEST, TestValue.TEST_NAME_TWO);
        userClient.updateUserNotAuth(StellarUserTwo)
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
        userClient.deleteUser(accessToken);
    }
}