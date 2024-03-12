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
public class ChangeBalanceTest {
    public static final String USER = "user";
    private final CustomerDao customerDao = new CustomerDaoImplMock();
    private CustomerServiceImpl customerService = new CustomerServiceImpl(customerDao);

    @BeforeEach
    void setUp() {
        customerDao.saveCustomer(USER);
    }

    @Test
    @DisplayName("Добавление нового покупателя из UserController\n")
    void changeBalanceTest() {
        boolean isSuccessful = customerService.saveCustomer(USER);

        Assertions.assertTrue(isSuccessful, "Не удалось создать customer");

        Integer balance = customerService.getBalance(USER);
        Assertions.assertEquals(0, balance, "Баланс не обновился");
    }
}
