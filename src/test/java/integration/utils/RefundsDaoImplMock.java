package integration.utils;

import lombok.*;
import ru.scheredin.dao.RefundsDao;
import ru.scheredin.dto.Refund;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RefundsDaoImplMock implements RefundsDao {
    private final Map<Integer, Refund> map = new HashMap<>();
    private Integer id = 0;

    @Override
    public boolean createRefund(Integer orderId, String description, Integer employeeId) {
        Refund refund = new Refund(id, orderId, description, false, employeeId);
        map.put(id++,refund);
        return true;
    }

    @Override
    public List<Refund> findByCustomerLogin(String login) {
        return map.values().stream().toList();
    }

    @Override
    public List<Refund> findByEmployeeLogin(String login) {
        return map.values().stream().toList();
    }

    @Override
    public boolean approveRefund(Integer refundId) {
        return map.computeIfPresent(refundId, (k, v) -> {
            v.setApproved(true);
            return v;
        }) != null;
    }

    @Override
    public List<Refund> findWithOrderId(Integer orderId) {
        return map
                .values()
                .stream()
                .filter(refund -> Objects.equals(refund.getOrder_id(), orderId))
                .toList();
    }
}
