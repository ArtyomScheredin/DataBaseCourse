package ru.scheredin.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.scheredin.dto.Refund;
import ru.scheredin.utils.DataBaseUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RefundsDao {
    private final DataBaseUtils dataBaseUtils;
    private static final String FIND_WITH_ORDER_ID_AND_CUSTOMER_LOGIN = """
            select * from refunds where order_id = %d and order_id in 
            (select order_id from orders 
            join users u on u.user_id = orders.customer_id where login='%s);""";


    public List<Refund> findWithOrderId(Integer orderId) {
        return dataBaseUtils.query(String.format("select * from refunds where order_id=%d;", orderId), Refund.class);
    }

    public List<Refund> findWithOrderIdAndCustomerLogin(String login, Integer orderId) {
        return dataBaseUtils.query(String.format(FIND_WITH_ORDER_ID_AND_CUSTOMER_LOGIN, orderId, login),
                                   Refund.class);
    }

    public boolean createRefund(Integer orderId, String description, Integer employeeId) {
        return dataBaseUtils.execute(String.format("insert into refunds (order_id, description, approved, employee_id)\n" +
                                                         "values (%d, '%s',false,%d);", orderId, description, employeeId)) == 1;
    }

    public List<Refund> findByCustomerLogin(String login) {
        return dataBaseUtils.query(String.format("""
            select * from refunds where order_id in 
            (select order_id from orders 
            join users u on u.user_id = orders.customer_id where login='%s);""", login), Refund.class);
    }

    public List<Refund> findByEmployeeLogin(String login) {
        return dataBaseUtils.query(String.format("""
                                                    select * from refunds where employee_id in
                                                    select user_id from users where login='%s');""", login), Refund.class);
    }
}
