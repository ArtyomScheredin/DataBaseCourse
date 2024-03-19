package ru.scheredin.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.scheredin.utils.DataBaseUtils;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomerDaoImpl implements CustomerDao {
    private final DataBaseUtils dataBaseUtils;

    @Override
    public void saveCustomer(String login) {
        //do nothin
    }

    @Override
    public List<String> getCustomers() {
        //do nothin
        return Collections.emptyList();
    }

    @Override
    public Integer getBalance(String login) {
        return dataBaseUtils.querySingle(
                String.format("select balance from customers join users u using(user_id) where login='%s';", login),
                r -> r.getInt("balance"));
    }

    @Override
    public boolean updateBalance(String login, Integer newBalance) {
        return dataBaseUtils.execute(String.format(
                "update customers set balance=%d where customers.user_id in (select user_id from users where login='%s');",
                newBalance, login)) == 1;
    }
}
