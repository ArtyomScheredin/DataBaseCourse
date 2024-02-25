package ru.scheredin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.CustomerDao;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerDao customerDao;
    public Integer getBalance(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalStateException("Wrong login");
        }else {
            return customerDao.getBalance(login);
        }
    }

    public boolean updateBalance(String login, Integer newBalance) {
        if (login == null || login.isBlank()) {
            throw new IllegalStateException("Wrong login");
        }else if (newBalance < 0){
            throw new IllegalArgumentException("Negative balance");
        }
        return customerDao.updateBalance(login, newBalance);
    }
}
