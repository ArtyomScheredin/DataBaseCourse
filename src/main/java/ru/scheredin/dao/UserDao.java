package ru.scheredin.dao;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDao {
    public UserDetails findUserByLogin(String login);
    public Integer findUserIdByLogin(String login);
}
