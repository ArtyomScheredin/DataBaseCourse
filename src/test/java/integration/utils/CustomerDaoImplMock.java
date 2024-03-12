package integration.utils;

import ru.scheredin.dao.CustomerDao;

import java.util.HashMap;
import java.util.Map;

public class CustomerDaoImplMock implements CustomerDao {

    private final Map<String, Integer> map = new HashMap<>();

    @Override
    public void saveCustomer(String login) {
        map.put(login, 0);
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
