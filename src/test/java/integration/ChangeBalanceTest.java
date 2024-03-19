package integration;

import integration.utils.CustomerDaoImplMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.scheredin.dao.CustomerDao;
import ru.scheredin.services.CustomerServiceImpl;



@Tags({
        @Tag("integration"),
        @Tag("artyom")
})
public class ChangeBalanceTest {
    private static final Logger logger = LoggerFactory.getLogger(ChangeBalanceTest.class);
    public static final String USER = "user";
    private final CustomerDao customerDao = new CustomerDaoImplMock();
    private CustomerServiceImpl customerService = new CustomerServiceImpl(customerDao);

    @BeforeEach
    void setUp() {
        logger.info("Настройка тестового окружения");
        customerDao.saveCustomer(USER);
    }

    @Test
    @DisplayName("Добавление нового покупателя из UserController\n")
    void changeBalanceTest() {
        logger.info("Начало теста changeBalanceTest");
        boolean isSuccessful = customerService.saveCustomer(USER);
        logger.info("Покупатель успешно создан: " + isSuccessful);

        Assertions.assertTrue(isSuccessful, "Не удалось создать customer");

        Integer balance = customerService.getBalance(USER);
        logger.info("Получен текущий баланс: " + balance);

        Assertions.assertEquals(0, balance, "Баланс не обновился");

        logger.info("Тест changeBalanceTest завершен");
    }
}
