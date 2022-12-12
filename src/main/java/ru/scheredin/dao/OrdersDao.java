package ru.scheredin.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.scheredin.dto.Order;
import ru.scheredin.utils.DataBaseUtils;

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
}
