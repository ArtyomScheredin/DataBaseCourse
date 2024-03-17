package ru.scheredin.dao;

import ru.scheredin.dto.Order;

import java.util.Date;
import java.util.List;

public interface OrdersDao {
    public List<Order> getOrdersByLogin(String login);
    public Integer createOrder(Integer userId, Date orderDate, Date receiveDate);
    public Integer addProduct(Integer orderId, Integer productId, Integer quantity);
}
