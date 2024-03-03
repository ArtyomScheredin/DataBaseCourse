package ru.scheredin.utils;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.scheredin.config.SecurityConfig;
@Tag("artyom")
public class SecurityConfigTest {

    @Test
    public void testAuthenticationProvider() {
        // Создание макета зависимостей
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        SecurityConfig securityConfig = new SecurityConfig(null, userDetailsService);

        // Тестирование создания провайдера аутентификации
        AuthenticationProvider authenticationProvider = securityConfig.authenticationProvider();
        assert authenticationProvider != null;
    }

    @Test
    public void testAuthenticationManager() throws Exception {
        // Создание макета зависимостей
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(config.getAuthenticationManager()).thenReturn(manager);
        SecurityConfig securityConfig = new SecurityConfig(null, null);

        // Тестирование создания менеджера аутентификации
        AuthenticationManager authenticationManager = securityConfig.authenticationManager(config);
        assert authenticationManager != null;
    }

    @Test
    public void testPasswordEncoder() {
        // Тестирование создания объекта кодировщика паролей
        SecurityConfig securityConfig = new SecurityConfig(null, null);
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assert passwordEncoder != null;
    }
}
