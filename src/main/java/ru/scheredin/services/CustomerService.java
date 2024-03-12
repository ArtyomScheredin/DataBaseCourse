package ru.scheredin.services;

public interface CustomerService {
    Integer getBalance(String login);

    boolean updateBalance(String login, Integer newBalance);

    boolean saveCustomer(String login);
}
