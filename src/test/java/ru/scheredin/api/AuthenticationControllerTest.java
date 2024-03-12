package ru.scheredin.api;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.scheredin.config.JwtUtils;
import ru.scheredin.dto.AuthenticationRequest;

@Tag("lera")
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthenticationProvider authenticationProvider;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешная аутентификация с валидными учетными данными")
    public void whenAuthenticateWithValidCredentials_thenReturnsToken() throws JsonProcessingException {
        AuthenticationRequest request = new AuthenticationRequest("user", "password");
        UserDetails mockUser = mock(UserDetails.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUser);
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("token");
        when(jwtUtils.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);

        ResponseEntity<String> result = authenticationController.authenticate(request, response);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Неудачная аутентификация с невалидными учетными данными")
    public void whenAuthenticateWithInvalidCredentials_thenReturnsUnauthorized() throws JsonProcessingException {
        AuthenticationRequest request = new AuthenticationRequest("user", "wrongpassword");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(null);

        ResponseEntity<String> result = authenticationController.authenticate(request, response);

        assertEquals(401, result.getStatusCodeValue());
        assertNull(result.getBody());
    }

    @Test
    @DisplayName("Возвращает ошибку, если токен недействителен")
    public void whenTokenIsInvalid_thenReturnsErrorResponse() throws JsonProcessingException {
        AuthenticationRequest request = new AuthenticationRequest("user", "password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails mockUser = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUser);
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("invalid_token");
        when(jwtUtils.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(false);

        ResponseEntity<String> result = authenticationController.authenticate(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getStatusCodeValue());
    }

    @Test
    @DisplayName("Добавляет куки в ответ при успешной аутентификации")
    public void whenAuthenticated_thenAddsCookieToResponse() throws JsonProcessingException {
        AuthenticationRequest request = new AuthenticationRequest("user", "password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails mockUser = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUser);
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("valid_token");
        when(jwtUtils.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);

        authenticationController.authenticate(request, response);

        Cookie cookie = response.getCookie("jwt");
        assertNotNull(cookie);
        assertEquals("valid_token", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.getMaxAge() > 0);
        assertFalse(cookie.isHttpOnly());
        assertFalse(cookie.getSecure());
    }
}
