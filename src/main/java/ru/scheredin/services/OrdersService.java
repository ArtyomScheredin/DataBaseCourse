package ru.scheredin.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.OrdersDao;
import ru.scheredin.dao.RefundsDao;
import ru.scheredin.dao.UserDao;
import ru.scheredin.dto.Order;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class OrdersService {
    private final OrdersDao ordersDao;
    private final ProductsService productsService;
    private final UserDao userDao;


    public List<Order> getOrders(String name) {
        return ordersDao.getOrdersByLogin(name);
    }

    public Integer createOrder(Map<Integer, Integer> products, String login) {
        Integer userId = userDao.findUserIdByLogin(login);
        HashSet<Integer> availableProducts = new HashSet<>(productsService.findAllProductsNotDiscontinued());
        if (userId == null || !availableProducts.containsAll(
                products.keySet())) {
            return null;
        }
        Date orderDate = new Date();
        ordersDao.createOrder(userId, orderDate, null);
        int orderId = ordersDao.getOrdersByLogin(login).stream().max(Comparator.comparingInt(Order::getOrder_id))
                .orElseThrow(() -> new RuntimeException("Странно, но заказ не создался"))
                .getOrder_id();
        products.forEach((productId, quantity) -> ordersDao.addProduct(orderId, productId, quantity));
        return orderId;
    }

}
