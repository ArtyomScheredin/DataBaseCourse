package ru.scheredin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.CustomerDao;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerDao customerDao;
    public Integer getBalance(String login) {
        return customerDao.getBalance(login);
    }

    public boolean updateBalance(String login, Integer newBalance) {
        return customerDao.updateBalance(login, newBalance);
    }
}
