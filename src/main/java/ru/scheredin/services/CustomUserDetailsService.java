package ru.scheredin.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.UserDaoImpl;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDaoImpl userDaoImpl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDaoImpl.findUserByLogin(username);
    }
}
