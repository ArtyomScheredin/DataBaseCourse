package integration.utils;

import ru.scheredin.dao.OrdersDao;
import ru.scheredin.dto.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OrdersDaoImplMock implements OrdersDao {
    @Override
    public List<Order> getOrdersByLogin(String login) {
        List<Order> orderList = new ArrayList<>();
        if(Objects.equals(login, "Test1")){
            orderList.add(new Order(1, "1", "16.03.2024", "16.03.2024"));
            orderList.add(new Order(2, "1", "16.03.2024", "16.03.2024"));
        }
        if(Objects.equals(login, "Test2"))
            orderList.add(new Order(2, "2", "16.03.2024", "16.03.2024"));

        return orderList;
    }

    @Override
    public Integer createOrder(Integer userId, Date orderDate, Date receiveDate) {
        if(userId == 1)
            return 1;
        if(userId == 2)
            return 2;
        return null;
    }

    @Override
    public Integer addProduct(Integer orderId, Integer productId, Integer quantity) {
        return null;
    }
}
