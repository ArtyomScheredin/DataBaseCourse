package ru.scheredin.dao;

import java.util.List;

public interface CustomerDao {
    void saveCustomer(String login);

    List<String> getCustomers();

    Integer getBalance(String login);

    boolean updateBalance(String login, Integer newBalance);
}
