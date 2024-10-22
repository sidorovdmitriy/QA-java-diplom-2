import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.GetIngredientId;
import model.StellarOrder;
import model.StellarUser;
import order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testvalue.TestValue;
import user.UserClient;

import java.util.ArrayList;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * 4. Создание заказа:
 */
public class CreateOrderTest {
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Post-запрос  /api/orders")
    @Step("Создание заказа")
    public void createOrderWithoutAuth() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(new GetIngredientId().getid("Флюоресцентная булка R2-D3"));
        ingredients.add(new GetIngredientId().getid("Мини-салат Экзо-Плантаго"));
        ingredients.add(new GetIngredientId().getid("Хрустящие минеральные кольца"));
        StellarOrder StellarOrder = new StellarOrder(ingredients);
        orderClient.orderWithoutAuth(StellarOrder)
                .assertThat().statusCode(HTTP_OK);
    }
    @Test
    @DisplayName("Создание заказа без авторизации, c неверным хешем")
    @Description("Post-запрос  /api/orders")
    @Step("Создание заказа")
    public void createOrderWithoutAuthErrorHash() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TestValue.BAD_BUN_TEST);
        ingredients.add(new GetIngredientId().getid("Хрустящие минеральные кольца"));
        StellarOrder StellarOrder = new StellarOrder(ingredients);
        orderClient.orderWithoutAuth(StellarOrder)
                .assertThat().statusCode(HTTP_INTERNAL_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без авторизации, без ингредиентов")
    @Description("Post-запрос /api/orders")
    @Step("Создание заказа")
    public void createOrderWithoutAuthNoIngredient() {
        StellarOrder StellarOrder = new StellarOrder(null);
        ValidatableResponse response = orderClient.orderWithoutAuth(StellarOrder)
                .assertThat().statusCode(HTTP_BAD_REQUEST);
        response.assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Post-запрос  /api/orders")
    @Step("Создание заказа")
    public void createOrderWithAuth() {
        StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        ValidatableResponse createResponse = userClient.createUser(StellarUser).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = createResponse.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        ArrayList<String> ingredients = new ArrayList<>();
        GetIngredientId id = new GetIngredientId();
        ingredients.add(new GetIngredientId().getid("Флюоресцентная булка R2-D3"));
        ingredients.add(new GetIngredientId().getid("Мини-салат Экзо-Плантаго"));
        ingredients.add(new GetIngredientId().getid("Хрустящие минеральные кольца"));
        StellarOrder StellarOrder = new StellarOrder(ingredients);
        ValidatableResponse response = orderClient.orderWithAuth(accessToken, StellarOrder)
                .assertThat().statusCode(HTTP_OK);
        response.assertThat().body("order.owner.name", equalTo(TestValue.TEST_NAME_ONE))
                .and()
                .body("order.owner.email", equalTo(TestValue.LOGIN_ONE_TEST));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, без ингредиентов")
    @Description("Post-запрос /api/orders")
    @Step("Создание заказа")
    public void createOrderWithAuthNoIngredient() {
        StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        ValidatableResponse createResponse = userClient.createUser(StellarUser).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = createResponse.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        StellarOrder StellarOrder = new StellarOrder(null);
        ValidatableResponse response = orderClient.orderWithAuth(accessToken, StellarOrder)
                .assertThat().statusCode(HTTP_BAD_REQUEST);
        response.assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с неверным хешем")
    @Description("Post-запрос /api/orders")
    @Step("Создание заказа")
    public void createOrderWithAuthErrorHash() {
        StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
        ValidatableResponse createResponse = userClient.createUser(StellarUser).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = createResponse.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TestValue.BAD_BUN_TEST);
        ingredients.add(new GetIngredientId().getid("Хрустящие минеральные кольца"));
        StellarOrder StellarOrder = new StellarOrder(ingredients);
        orderClient.orderWithAuth(accessToken, StellarOrder)
                .assertThat().statusCode(HTTP_INTERNAL_ERROR);
    }

    @After
    public void clearData() {
        try {
            StellarUser StellarUser = new StellarUser(TestValue.LOGIN_ONE_TEST, TestValue.PASSWORD_ONE_TEST, TestValue.TEST_NAME_ONE);
            ValidatableResponse loginResponse = userClient.userLogin(StellarUser);
            String accessTokenWithBearer = loginResponse.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
        } catch (Exception e) {
            System.out.println("Пользователь не удалился");
        }
    }
}