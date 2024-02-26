package ru.scheredin.config.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.scheredin.dao.UserDao;
import ru.scheredin.services.CustomUserDetailsService;
import java.util.Collections;


import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("lera")
class CustomUserDetailsServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Загрузка пользователя по имени пользователя")
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        String username = "existingUser";
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDao.findUserByLogin(username)).thenReturn(mockUserDetails);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(mockUserDetails, userDetails);
    }

    @Test
    @DisplayName("Загрузка пользователя возвращает правильные данные")
    void loadUserByUsername_UserExists_ReturnsCorrectUserDetails() {
        String username = "validUser";
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDao.findUserByLogin(username)).thenReturn(mockUserDetails);
        when(mockUserDetails.getUsername()).thenReturn(username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
    }

    @Test
    @DisplayName("Загрузка пользователя вызывает исключение, если пользователь не найден")
    void loadUserByUsername_UserNotExists_ThrowsUsernameNotFoundException() {
        String username = "nonExistingUser";
        when(userDao.findUserByLogin(username)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }

}

