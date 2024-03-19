package integration;

import integration.utils.OrdersDaoImplMock;
import integration.utils.ProductsDaoImplMock;
import integration.utils.UserDaoImplMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.scheredin.dao.OrdersDao;
import ru.scheredin.dao.ProductsDao;
import ru.scheredin.dao.UserDao;
import ru.scheredin.services.OrdersServiceImpl;
import ru.scheredin.services.ProductsServiceImpl;
import ru.scheredin.utils.DataBaseUtils;

import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CreateOrderTest {
    private final DataBaseUtils dataBaseUtils = mock(DataBaseUtils.class);
    private final OrdersDao ordersDao = new OrdersDaoImplMock();
    private final ProductsDao productsDao = new ProductsDaoImplMock();
    private final UserDao userDao = new UserDaoImplMock();
    private final ProductsServiceImpl productsService = new ProductsServiceImpl(productsDao, dataBaseUtils);
    private final OrdersServiceImpl ordersService = new OrdersServiceImpl(ordersDao, productsService, userDao, dataBaseUtils);

    private final String login = "Test1";
    private final Integer balance = 500;

    private final Map<Integer, Integer> products = new HashMap<>();
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("Проверка создания заказа")
    void createOrderTest(){

        products.put(1, 2);

        when(dataBaseUtils.querySingle(eq(anyString()),
                t -> t.getInt(1)))
                .thenReturn(20);

        when(dataBaseUtils.querySingle(eq(anyString()),
                t -> t.getInt("balance")))
                .thenReturn(balance);

        Integer res_1 = ordersService.createOrder(products, null);
        //Integer res_2 = ordersService.createOrder(products, login);

        assertNull(res_1, "Метод отработал, не смотря на login: null");
    }
}
