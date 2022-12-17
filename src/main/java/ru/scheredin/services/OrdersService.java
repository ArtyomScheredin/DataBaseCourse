package ru.scheredin.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.OrdersDao;
import ru.scheredin.dao.UserDao;
import ru.scheredin.dto.Order;
import ru.scheredin.utils.DataBaseUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class OrdersService {
    private final OrdersDao ordersDao;
    private final ProductsService productsService;
    private final UserDao userDao;
    private final DataBaseUtils dataBaseUtils;


    public List<Order> getOrders(String name) {
        return ordersDao.getOrdersByLogin(name);
    }

    public Integer createOrder(Map<Integer, Integer> products, String login) {
        Integer userId = userDao.findUserIdByLogin(login);
        if (userId == null) {
            return null;
        }
        Date orderDate = new Date();
        ordersDao.createOrder(userId, orderDate, null);
        int orderId = ordersDao.getOrdersByLogin(login).stream().max(Comparator.comparingInt(Order::getOrder_id))
                .orElseThrow(() -> new RuntimeException("Странно, но заказ не создался"))
                .getOrder_id();
        products.forEach((productId, quantity) -> ordersDao.addProduct(orderId, productId, quantity));
        int sum = 0;
        for (Map.Entry<Integer, Integer> e : products.entrySet()) {
            sum += dataBaseUtils.querySingle(
                    String.format("select price from products where product_id=%d;", e.getKey()),
                    t -> t.getInt(1)) * e.getValue();
        }
        Integer balance = dataBaseUtils.querySingle(
                String.format("select balance from customers join users u on u.user_id = customers.user_id where login='%s';",login),
                t -> t.getInt("balance"));
        dataBaseUtils.execute(String.format(
                "update customers set balance=%d where customers.user_id in (select user_id from users where login='%s');",
                balance - sum, login));
        return orderId;
    }

}
