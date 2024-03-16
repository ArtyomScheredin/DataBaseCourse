package ru.scheredin.services;

import ru.scheredin.dto.Employee;
import ru.scheredin.dto.Refund;

import java.util.List;
import java.util.Objects;

public interface RefundsService {
    boolean isCouldBeRefunded(Integer orderId);

    boolean isOwner(String login, Integer orderId);

    boolean isAssignedEmployee(String login, Integer refundId);

    boolean createRefund(Integer order_id, String description);

    List<Refund> getMyRefunds(String login);

    List<Refund> getAssignedRefunds(String login);

    boolean approveRefund(Integer refundId);
}
