package ru.scheredin.services;

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
import ru.scheredin.dao.UserDaoImpl;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@Tag("lera")
class CustomUserDetailsServiceTest {

    @Mock
    private UserDaoImpl userDaoImpl;

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
        when(userDaoImpl.findUserByLogin(username)).thenReturn(mockUserDetails);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(mockUserDetails, userDetails);
    }

    @Test
    @DisplayName("Загрузка пользователя возвращает правильные данные")
    void loadUserByUsername_UserExists_ReturnsCorrectUserDetails() {
        String username = "validUser";
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDaoImpl.findUserByLogin(username)).thenReturn(mockUserDetails);
        when(mockUserDetails.getUsername()).thenReturn(username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
    }

    @Test
    @DisplayName("Загрузка пользователя вызывает исключение, если пользователь не найден")
    void loadUserByUsername_UserNotExists_ThrowsUsernameNotFoundException() {
        String username = "nonExistingUser";
        when(userDaoImpl.findUserByLogin(username)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }

    @Test
    @DisplayName("Test loadUserByUsername with existing user - Интеграционный Тест Лера" )
    public void testLoadUserByUsername_existingUser() {
        // Arrange
        String username = "test_user";
        String password = "password";
        String role = "USER";

        when(userDaoImpl.findUserByLogin(username)).thenReturn(
                new org.springframework.security.core.userdetails.User(
                        username, password, Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role))
                )
        );

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_" + role)
        ));
    }

}

