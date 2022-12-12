package ru.scheredin.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.OrdersDao;
import ru.scheredin.dto.Order;

import java.util.List;

@Service
@AllArgsConstructor
public class OrdersService {
    private final OrdersDao ordersDao;
    public List<Order> getOrders(String name) {
        return ordersDao.getOrdersByLogin(name);
    }
}
