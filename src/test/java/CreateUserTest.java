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

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * 1. Создание пользователя:
 */
public class CreateUserTest {
    private UserClient userClient;
    private ValidatableResponse loginResponse;
    private StellarUser StellarUser;

    @Before
    public void setUp() {
        userClient = new UserClient();
        StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        loginResponse = userClient.createUser(StellarUser);
    }

    @Test
    @DisplayName("Создать уникального пользователя")
    @Description("Post-запрос /api/v1/courier")
    @Step("Cоздание пользователя")
    public void createUniqueUserTest() {
        loginResponse.assertThat().statusCode(HTTP_OK);
        loginResponse.assertThat().body("user.email", equalTo(TestValue.LOGIN_ONE_TEST))
                .and()
                .assertThat().body("user.name", equalTo(TestValue.TEST_NAME_ONE));
        loginResponse.assertThat().body("accessToken", startsWith("Bearer "));
        loginResponse.assertThat().body("refreshToken", notNullValue());
        loginResponse.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    @Description("Post-запрос /api/v1/courier")
    @Step("Cоздание пользователя")
    public void createRegisteredUserTest() {
        ValidatableResponse responseTwo = userClient.createUser(StellarUser)
                .assertThat().statusCode(HTTP_FORBIDDEN);
        responseTwo.assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить пароль")
    @Description("Post-запрос /api/v1/courier")
    @Step("Cоздание пользователя")
    public void createUserWithoutPasswordTest() {
        try {
            ValidatableResponse createNoPasswordUserResponse = userClient.createUser(new StellarUser(TestValue.LOGIN_ONE_TEST, null, TestValue.TEST_NAME_ONE))
                    .assertThat().statusCode(HTTP_FORBIDDEN);
            createNoPasswordUserResponse.assertThat().body("success", equalTo(false))
                    .and().body("message", equalTo("Email, password and name are required fields"));
        } catch (Exception e) {
            ValidatableResponse createNoPasswordUserResponse = userClient.createUser(new StellarUser(TestValue.LOGIN_ONE_TEST, null, TestValue.TEST_NAME_ONE));
            String accessTokenWithBearer = createNoPasswordUserResponse.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("удален");
        }
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить почту")
    @Description("Post-запрос  /api/v1/courier")
    @Step("Создание пользователя")
    public void createUserWithoutEmailTest() {
        try {
            ValidatableResponse createNoEmailUserResponse = userClient.createUser(new StellarUser(null, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE))
                    .assertThat().statusCode(HTTP_FORBIDDEN);
            createNoEmailUserResponse.assertThat().body("success", equalTo(false))
                    .and().body("message", equalTo("Email, password and name are required fields"));
        } catch (Exception e) {
            ValidatableResponse createNoEmailUserResponse = userClient.createUser(new StellarUser(null, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE));
            String accessTokenWithBearer = createNoEmailUserResponse.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("удален");
        }

    }

    @Test
    @DisplayName("Создать пользователя и не заполнить имя пользователя")
    @Description("Post-запрос /api/v1/courier")
    @Step("Cоздание пользователя")
    public void createUserWithoutNameTest() {
        try {
            ValidatableResponse createNoNameUserResponse = userClient.createUser(new StellarUser(TestValue.TEST_NAME_ONE, TestValue.PASSWORD_ONE_TEST, null))
                    .assertThat().statusCode(HTTP_FORBIDDEN);
            createNoNameUserResponse.assertThat().body("success", equalTo(false))
                    .and().body("message", equalTo("Email, password and name are required fields"));
        } catch (Exception e) {
            ValidatableResponse createNoNameUserResponse = userClient.createUser(new StellarUser(TestValue.TEST_NAME_ONE, TestValue.PASSWORD_ONE_TEST, null));
            String accessTokenWithBearer = createNoNameUserResponse.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("удален");
        }
    }

    @After
    @Step("Удаление пользователя после прохождения теста")
    public void clearData() {
        try {
            String accessTokenWithBearer = loginResponse.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("удален");
        } catch (Exception e) {
            System.out.println("Пользователь не удалился");
        }
    }
}