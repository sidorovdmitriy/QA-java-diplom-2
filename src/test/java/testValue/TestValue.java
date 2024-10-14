package testValue;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Тестовые данные: login, password и т.д
 */
public class TestValue extends RandomStringUtils{
    public static final String
            LOGIN_ONE_TEST = randomAlphabetic(10).toLowerCase()+"@yandex.ru",
            LOGIN_TWO_TEST = randomAlphabetic(10).toLowerCase()+"@yandex.ru",
            PASSWORD_ONE_TEST = "123",
            PASSWORD_TWO_TEST = "123qwe",
            TEST_NAME_ONE = randomAlphabetic(10),
            TEST_NAME_TWO = randomAlphabetic(10),
            BUN_TEST = "61c0c5a71d1f82001bdaaa6d",
            BAD_BUN_TEST = "123",
            FILLING_ONE_TEST = "61c0c5a71d1f82001bdaaa79",
            FILLING_TWO_TEST = "61c0c5a71d1f82001bdaaa76";
}