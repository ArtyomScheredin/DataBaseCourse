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
    public static final int NEW_BALANCE = 255;
    private static final Logger logger = LoggerFactory.getLogger(ChangeBalanceTest.class);
    public static final String USER = "user";
    public static final int NEW_BALANCE_NEGATIVE = -255;
    private final CustomerDao customerDao = new CustomerDaoImplMock();
    private CustomerServiceImpl customerService = new CustomerServiceImpl(customerDao);

    @BeforeEach
    void setUp() {
        logger.info("Настройка тестового окружения");
        customerDao.saveCustomer(USER);
    }

    @Test
    @DisplayName("Изменение баланса пользователя")
    public void changeBalanceTest() {
        logger.info("Запуск теста на изменение баланса");
        customerService.saveCustomer(USER);

        boolean isSuccessful = customerService.updateBalance(USER, NEW_BALANCE);

        Assertions.assertTrue(isSuccessful, "Не удалось обновить баланс");
        logger.info("Баланс успешно обновлен");

        Integer balance = customerService.getBalance(USER);
        Assertions.assertEquals(balance, NEW_BALANCE, "Баланс не обновился");
        logger.info("Проверка успешности обновления баланса");
    }

    @Test
    @DisplayName("Изменение баланса на отрицательный")
    public void changeBalanceTest2() {
        logger.info("Запуск теста на изменение баланса");
        customerService.saveCustomer(USER);
        int newBalance = NEW_BALANCE_NEGATIVE;

        Assertions.assertThrows(IllegalArgumentException.class, () -> customerService.updateBalance(USER, newBalance)) ;
    }
}
