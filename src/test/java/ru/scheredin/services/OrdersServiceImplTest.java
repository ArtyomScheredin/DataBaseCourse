package ru.scheredin.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.scheredin.dao.OrdersDaoImpl;
import ru.scheredin.dao.UserDaoImpl;
import ru.scheredin.dto.Order;
import ru.scheredin.utils.DataBaseUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@Tag("lera")
public class OrdersServiceImplTest {

    @Mock
    private OrdersDaoImpl ordersDaoImpl;
    @Mock
    private ProductsServiceImpl productsServiceImpl;
    @Mock
    private UserDaoImpl userDaoImpl;
    @Mock
    private DataBaseUtils dataBaseUtils;

    @InjectMocks
    private OrdersServiceImpl ordersServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Получение заказов для существующего пользователя")
    public void whenUserExists_thenGetOrdersReturnsOrders() {
        String username = "existingUser";
        List<Order> expectedOrders = Arrays.asList(new Order(), new Order());
        when(ordersDaoImpl.getOrdersByLogin(username)).thenReturn(expectedOrders);

        List<Order> orders = ordersServiceImpl.getOrders(username);

        assertNotNull(orders);
        assertFalse(orders.isEmpty());
        assertEquals(expectedOrders, orders);
    }

    @Test
    @DisplayName("Получение заказов для несуществующего пользователя возвращает пустой список")
    public void whenUserNotExists_thenGetOrdersReturnsEmptyList() {
        String username = "nonExistingUser";
        when(ordersDaoImpl.getOrdersByLogin(username)).thenReturn(Collections.emptyList());

        List<Order> orders = ordersServiceImpl.getOrders(username);

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    @DisplayName("Создание заказа для несуществующего пользователя возвращает null")
    public void whenUserNotExists_thenCreateOrderReturnsNull() {
        String username = "nonExistingUser";
        Map<Integer, Integer> products = Map.of(1, 2);
        when(userDaoImpl.findUserIdByLogin(username)).thenReturn(null);

        Integer orderId = ordersServiceImpl.createOrder(products, username);

        assertNull(orderId);
    }

    @Test
    @DisplayName("Создание заказа для несуществующего пользователя возвращает null")
    void createOrderForNonExistingUser() {
        String username = "nonExistingUser";
        when(userDaoImpl.findUserIdByLogin(username)).thenReturn(null);

        Integer result = ordersServiceImpl.createOrder(Map.of(1, 2), username);

        assertNull(result);
    }

    @Test
    @DisplayName("Создание заказа когда обновление баланса вызывает ошибку")
    void createOrderUpdateBalanceThrowsException() {
        when(userDaoImpl.findUserIdByLogin("user")).thenReturn(1);
        when(ordersDaoImpl.getOrdersByLogin("user")).thenReturn(List.of(new Order())); // Provide a dummy order
        doThrow(RuntimeException.class).when(dataBaseUtils).execute(anyString());

        assertThrows(RuntimeException.class, () -> {
            ordersServiceImpl.createOrder(Map.of(1, 2), "user");
        });
    }
}

