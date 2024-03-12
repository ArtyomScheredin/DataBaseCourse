package ru.scheredin.dao;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import ru.scheredin.dto.Order;
import ru.scheredin.utils.DataBaseUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Tag("denis")
public class OrdersDaoTest {
    @Mock
    private DataBaseUtils dataBaseUtils;
    @Mock
    private List<Order> orders;
    @Autowired
    private OrdersDao underTest;
    private AutoCloseable autoCloseable;
    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new OrdersDao(dataBaseUtils);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Тест проверки получения заказов по логину")
    void getOrdersByLoginTest(){
        String login = "testLogin";
        String testString = String.format(
                "select * from orders where customer_id in (select user_id from users where users.login='%s');", login);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        when(dataBaseUtils.query(argumentCaptor.capture(), eq(Order.class))).thenReturn(orders);
        underTest.getOrdersByLogin(login);

        assertEquals(argumentCaptor.getValue(), testString);
    }
    @Test
    @DisplayName("Тест проверки создания заказа по userId, orderDate, receiveDate, если userId == null")
    void createOrderTest(){
        assertNull(underTest.createOrder(null, null, null));
    }
    @Test
    @DisplayName("Тест проверки создания заказа по userId, orderDate, receiveDate")
    void createOrderTest1(){
        Integer userId = 0;
        Date orderDate = new Date();
        Date receiveDate = new Date();
        String testString = String.format("insert into orders (customer_id, order_date, recieve_date) values (%d,%s,%s);",
                userId, orderDate == null ? "NULL" : '\'' + orderDate.toString() + '\'',
                receiveDate == null ? "NULL" : '\'' + receiveDate.toString() + '\'');

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        when(dataBaseUtils.execute(argumentCaptor.capture())).thenReturn(100);
        Integer res = underTest.createOrder(userId, orderDate, receiveDate);

        assertEquals(res, 100);
        assertEquals(argumentCaptor.getValue(), testString);
    }
    @Test
    @DisplayName("Тест для проверки добавления продукта")
    void addProductTest(){
        Integer orderId = 0;
        Integer productId = 1;
        Integer quantity = 2;
        String testString = String.format(
                "insert into products_orders_link (orders_id, product_id, quantity) values ('%d','%d','%d');",
                orderId, productId, quantity);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        when(dataBaseUtils.execute(argumentCaptor.capture())).thenReturn(100);

        Integer res = underTest.addProduct(orderId, productId, quantity);

        Integer res1 = underTest.addProduct(null, productId, quantity);
        Integer res2 = underTest.addProduct(orderId, null, quantity);
        Integer res3 = underTest.addProduct(orderId, productId, null);

        assertEquals(res, 100);
        assertEquals(argumentCaptor.getValue(), testString);

        assertNull(res1);
        assertNull(res2);
        assertNull(res3);
    }
}
