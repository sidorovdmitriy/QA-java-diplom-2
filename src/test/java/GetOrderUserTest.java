import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.GetIngredientId;
import model.StellarUser;
import model.StellarOrder;
import order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testvalue.TestValue;
import user.UserClient;

import java.util.ArrayList;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

/**
 5. Получение заказов конкретного пользователя:
 */
public class GetOrderUserTest {
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Get-запрос api/orders")
    @Step("Получение заказов")
    public void getOrderAuthUser() {
        StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        ValidatableResponse createResponse = userClient.createUser(StellarUser).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = createResponse.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(new GetIngredientId().getid("Флюоресцентная булка R2-D3"));
        ingredients.add(new GetIngredientId().getid("Мини-салат Экзо-Плантаго"));
        ingredients.add(new GetIngredientId().getid("Хрустящие минеральные кольца"));
        StellarOrder StellarOrder = new StellarOrder(ingredients);
        orderClient.orderWithAuth(accessToken, StellarOrder)
                .assertThat().statusCode(HTTP_OK);
        ValidatableResponse getOrdersResponse = orderClient.getOrderUserAuth(accessToken)
                .assertThat().statusCode(HTTP_OK);
        getOrdersResponse.assertThat().body("success", equalTo(true))
                .and()
                .body("orders", not(ingredients.isEmpty()));
    }

    @Test
    @DisplayName("Получение заказов не авторизованного пользователя")
    @Description("Get-запрос api/orders")
    @Step("Получение заказов")
    public void getOrderNotAuthUser() {
        StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        ValidatableResponse createResponse = userClient.createUser(StellarUser).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = createResponse.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(new GetIngredientId().getid("Флюоресцентная булка R2-D3"));
        ingredients.add(new GetIngredientId().getid("Мини-салат Экзо-Плантаго"));
        ingredients.add(new GetIngredientId().getid("Хрустящие минеральные кольца"));
        StellarOrder StellarOrder = new StellarOrder(ingredients);
        orderClient.orderWithAuth(accessToken, StellarOrder)
                .assertThat().statusCode(HTTP_OK);
        ValidatableResponse getOrdersResponse = orderClient.getOrderUserNotAuth()
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
        getOrdersResponse.assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void clearData() {
        try {
            StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
            ValidatableResponse loginResponse = userClient.userLogin(StellarUser);
            String accessTokenWithBearer = loginResponse.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("удален");
        } catch (Exception e) {
            System.out.println("Завершилось без удаления");
        }
    }
}