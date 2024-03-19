package integration;

import integration.utils.CustomerDaoImplMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import ru.scheredin.dao.CustomerDao;
import ru.scheredin.services.CustomerServiceImpl;

import java.util.List;
import java.util.logging.Logger;

@Tags({
        @Tag("integration"),
        @Tag("artyom")
})
public class CreateCustomerTest {

    private static final Logger logger = Logger.getLogger(CreateCustomerTest.class.getName());
    public static final String USER = "user";
    private final CustomerDao customerDao = new CustomerDaoImplMock();
    private final CustomerServiceImpl customerService = new CustomerServiceImpl(customerDao);

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

        List<String> customers = customerService.getCustomers();


        Assertions.assertTrue(customers.contains(USER));

    }
}
