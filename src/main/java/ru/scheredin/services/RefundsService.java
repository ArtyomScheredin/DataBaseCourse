package ru.scheredin.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.RefundsDao;
import ru.scheredin.dto.Refund;

import java.util.List;

@Service
@AllArgsConstructor
public class RefundsService {
    private final RefundsDao refundsDao;
    private final EmployeesService employeesService;

    public boolean isCouldBeRefunded(Integer orderId) {
        return refundsDao.findWithOrderId(orderId).isEmpty();
    }

    public boolean isOwner(String login, Integer orderId) {
        return refundsDao.findByCustomerLogin(login).stream().anyMatch(e -> e.getOrder_id().equals(orderId));
    }

    public boolean isAssignedEmployee(String login, Integer refundId) {
        return refundsDao.findByCustomerLogin(login).stream().anyMatch(e -> e.getRefund_id().equals(refundId));
    }

    public void createRefund(Integer order_id, String description) {
        refundsDao.createRefund(order_id, description, employeesService.getRandomEmployee().getUser_id());
    }

    public List<Refund> getMyRefunds(String login) {
        return refundsDao.findByCustomerLogin(login);
    }

    public List<Refund> getAssignedRefunds(String login) {
        return refundsDao.findByEmployeeLogin(login);
    }

    public int approveRefund(Integer refundId) {
        return refundsDao.approveRefund(refundId);
    }
}
