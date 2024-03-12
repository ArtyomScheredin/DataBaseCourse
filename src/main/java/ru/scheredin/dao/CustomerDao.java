package ru.scheredin.dao;

public interface CustomerDao {
    void saveCustomer(String login);

    Integer getBalance(String login);

    boolean updateBalance(String login, Integer newBalance);
}
