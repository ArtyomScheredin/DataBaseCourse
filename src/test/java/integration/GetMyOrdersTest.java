package integration;

import integration.utils.OrdersDaoImplMock;
import integration.utils.ProductsDaoImplMock;
import integration.utils.UserDaoImplMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.scheredin.dao.OrdersDao;
import ru.scheredin.dao.ProductsDao;
import ru.scheredin.dao.UserDao;
import ru.scheredin.dto.Order;
import ru.scheredin.services.OrdersServiceImpl;
import ru.scheredin.services.ProductsServiceImpl;
import ru.scheredin.utils.DataBaseUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class GetMyOrdersTest {
    private final DataBaseUtils dataBaseUtils = mock(DataBaseUtils.class);
    private final OrdersDao ordersDao = new OrdersDaoImplMock();
    private final ProductsDao productsDao = new ProductsDaoImplMock();
    private final UserDao userDao = new UserDaoImplMock();
    private final ProductsServiceImpl productsService = new ProductsServiceImpl(productsDao, dataBaseUtils);
    private final OrdersServiceImpl ordersService = new OrdersServiceImpl(ordersDao, productsService, userDao, dataBaseUtils);
    private final String login_1 = "Test1";
    private final String login_2 = "Test2";
    @Test
    @DisplayName("Проверка получения заказа")
    void GetMyOrdersTest() {
        List<Order> res_1 = ordersService.getOrders(login_1);
        List<Order> res_2 = ordersService.getOrders(login_2);

        assertEquals(2, res_1.size(), "Не совпадает кол-во заказов у Test1");
        assertEquals(1, res_2.size(), "Не совпадает кол-во заказов у Test2");
    }
}
