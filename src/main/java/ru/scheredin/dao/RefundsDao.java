package ru.scheredin.dao;

import ru.scheredin.dto.Refund;

import java.util.Collections;
import java.util.List;

public interface RefundsDao {
    List<Refund> findWithOrderId(Integer orderId);

    default List<Refund> findWithOrderIdAndCustomerLogin(String login, Integer orderId){
        return Collections.EMPTY_LIST;
    }

    boolean createRefund(Integer orderId, String description, Integer employeeId);

    List<Refund> findByCustomerLogin(String login);

    List<Refund> findByEmployeeLogin(String login);


    default List<Refund> getAll(){
        return Collections.EMPTY_LIST;
    }

    boolean approveRefund(Integer refundId);
}
