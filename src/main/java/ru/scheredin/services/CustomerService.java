package ru.scheredin.services;

import java.util.List;

public interface CustomerService {
    Integer getBalance(String login);

    boolean updateBalance(String login, Integer newBalance);

    boolean saveCustomer(String login);
    List<String> getCustomers();
}
