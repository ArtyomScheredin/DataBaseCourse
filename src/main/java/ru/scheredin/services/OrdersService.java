package ru.scheredin.services;

import ru.scheredin.dto.Order;

import java.util.List;
import java.util.Map;

public interface OrdersService {
    public List<Order> getOrders(String name);
    public Integer createOrder(Map<Integer, Integer> products, String login);
}
