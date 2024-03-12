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

@Tags({
        @Tag("integration"),
        @Tag("artyom")
})
public class CreateCustomerTest {

    public static final String USER = "user";
    private final CustomerDao customerDao = new CustomerDaoImplMock();
    private final CustomerServiceImpl customerService = new CustomerServiceImpl(customerDao);

    @BeforeEach
    void setUp() {
        customerDao.saveCustomer(USER);
    }

    @Test
    @DisplayName("Добавление нового покупателя из UserController\n")
    void changeBalanceTest() {
        customerService.saveCustomer(USER);
        int newBalance = 255;

        boolean isSuccessful = customerService.updateBalance(USER, newBalance);

        Assertions.assertTrue(isSuccessful, "Не удалось обновить баланс");

        Integer balance = customerService.getBalance(USER);
        Assertions.assertEquals(balance, newBalance, "Баланс не обновился");
    }
}
