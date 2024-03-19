package integration.utils;

import ru.scheredin.dao.CustomerDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomerDaoImplMock implements CustomerDao {

    private final Map<String, Integer> map = new HashMap<>();

    @Override
    public void saveCustomer(String login) {
        map.put(login, 0);
    }

    @Override
    public List<String> getCustomers() {
        return map.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public Integer getBalance(String login) {
        return map.get(login);
    }

    @Override
    public boolean updateBalance(String login, Integer newBalance) {
        return map.computeIfPresent(login, (k, v) -> newBalance) != null;
    }
}
