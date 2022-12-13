package ru.scheredin.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.scheredin.dto.Order;
import ru.scheredin.utils.DataBaseUtils;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrdersDao {
    private final DataBaseUtils dataBaseUtils;

    public List<Order> getOrdersByLogin(String login) {
        return dataBaseUtils.query(String.format(
                                           "select * from orders where customer_id in (select user_id from users where users.login='%s');", login),
                                   Order.class);
    }

    public Integer createOrder(Integer userId, Date orderDate, Date receiveDate) {
        if (userId == null) {
            return null;
        }
        return dataBaseUtils.execute(
                String.format("insert into orders (customer_id, order_date, recieve_date) values (%d,%s,%s);",
                              userId, orderDate == null ? "NULL" : '\'' + orderDate.toString() + '\'',
                              receiveDate == null ? "NULL" : '\'' + receiveDate.toString() + '\''));
    }

    public Integer addProduct(Integer orderId, Integer productId, Integer quantity) {
        if (orderId == null || productId == null || quantity == null) {
            return null;
        }
        return dataBaseUtils.execute(
                String.format(
                        "insert into products_orders_link (orders_id, product_id, quantity) values ('%d','%d','%d');",
                        orderId, productId, quantity));

    }
}
