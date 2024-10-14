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

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 2. Логин пользователя
 */
public class LoginUserTest {
    private UserClient userClient;
    private ValidatableResponse loginResponse;
    private StellarUser StellarUser;


    @Before
    @Step("Предусловие.Создание пользователя")
    public void setUp() {
        userClient = new UserClient();
        StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        loginResponse = userClient.createUser(StellarUser);
    }

    @Test
    @DisplayName("Логин под существующим пользователем. Ответ 200 / Проверка body")
    @Description("Post запрос на ручку /api/auth/login")
    @Step("Основной шаг - логин пользователя")
    public void loginWithUserTrueAndCheckBody() {
        loginResponse.assertThat().statusCode(HTTP_OK);
        loginResponse.assertThat().body("success", equalTo(true));
        loginResponse.assertThat().body("accessToken", startsWith("Bearer "))
                .and()
                .body("refreshToken", notNullValue());
        loginResponse.assertThat().body("user.email", equalTo(TestValue.LOGIN_ONE_TEST))
                .and()
                .body("user.name", equalTo(TestValue.TEST_NAME_ONE));
    }

    @Test
    @DisplayName("Логин под неверным именем почты. Ответ 401 / Проверка body")
    @Description("Post запрос на ручку /api/auth/login")
    @Step("Основной шаг - логин пользователя")
    public void loginWithUserFalseAndCheckBody() {
        StellarUser.setEmail(TestValue.LOGIN_TWO_TEST);
        userClient.userLogin(StellarUser).assertThat().statusCode(HTTP_UNAUTHORIZED);
        userClient.userLogin(StellarUser)
                .assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин под неверным паролем. Ответ 401 / Проверка body")
    @Description("Post запрос на ручку /api/auth/login")
    @Step("Основной шаг - логин пользователя")
    public void loginWithUserFalsePasswordAndCheckBody() {
        StellarUser.setPassword(TestValue.PASSWORD_TWO_TEST);
        userClient.userLogin(StellarUser)
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
        userClient.userLogin(StellarUser)
                .assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    @Step("Постусловие.Удаление пользователя")
    public void clearData() {
        try {
            String accessTokenWithBearer = loginResponse.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("удален");
        } catch (Exception e) {
            System.out.println("Пользователь не удалился. Возможно ошибка при создании");
        }
    }
}