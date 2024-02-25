package ru.scheredin.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.RefundsDao;
import ru.scheredin.dto.Employee;
import ru.scheredin.dto.Refund;

import java.util.List;
import java.util.Objects;

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

    public boolean createRefund(Integer order_id, String description) {
        if(refundsDao.findWithOrderId(order_id).isEmpty()){
            return false;
        }else if (description.isEmpty()){
            return false;
        }else{
            Employee employee = employeesService.getRandomEmployee();
            return employee != null && refundsDao.createRefund(order_id, description, employee.getUser_id());
        }
    }

    public List<Refund> getMyRefunds(String login) {
        return refundsDao.findByCustomerLogin(login);
    }

    public List<Refund> getAssignedRefunds(String login) {
        return refundsDao.findByEmployeeLogin(login);
    }

    public boolean approveRefund(Integer refundId) {
        if(refundsDao.getAll().stream()
                .filter(r-> Objects.equals(r.getRefund_id(), refundId))
                .toList().isEmpty()){
            return false;
        }else{
            return refundsDao.approveRefund(refundId);
        }
    }
}
