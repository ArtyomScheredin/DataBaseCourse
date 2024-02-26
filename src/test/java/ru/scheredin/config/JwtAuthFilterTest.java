package ru.scheredin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("artyom")
@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilterTest.class);

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;
    @Mock
    private UserDetails userDetails;
    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    private JwtAuthFilter jwtAuthFilter;
    private final static String JWT_TOKEN = "valid_jwt_token";

    @BeforeEach
    void setUp() {
        // Логгирование настройки моков
        LOGGER.info(() -> "Установка моков и настройка поведения:");

        // Мокирование поведения для извлечения имени пользователя из JWT-токена
        LOGGER.info(() -> "Мокирование extractUsername(JWT_TOKEN)");
        lenient().when(jwtUtils.extractUsername(Mockito.eq(JWT_TOKEN))).thenReturn("username");

        // Мокирование загрузки пользовательских данных по имени пользователя
        LOGGER.info(() -> "Мокирование loadUserByUsername(\"username\")");
        lenient().when(userDetailsService.loadUserByUsername(Mockito.eq("username"))).thenReturn(userDetails);

        // Создание мок-куки
        LOGGER.info(() -> "Создание мок-куки с именем \"jwt\" и значением JWT_TOKEN");
        Cookie mockCookie = mock(Cookie.class);
        lenient().when(mockCookie.getName()).thenReturn("jwt");
        lenient().when(mockCookie.getValue()).thenReturn(JWT_TOKEN);
        lenient().when(request.getCookies()).thenReturn(new Cookie[]{mockCookie});
    }
    @AfterEach
    void cleanUp() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @DisplayName("JWT-токен действителен. Фильтр вызывает последующую фильтрацию")
    @Test
    void testDoFilterInternalValidTokenDoConsequentFiltering() throws ServletException, IOException {
        // Мокируем поведение
        lenient().when(jwtUtils.isTokenValid(JWT_TOKEN, userDetails)).thenReturn(true);
        jwtAuthFilter = new JwtAuthFilter(userDetailsService, jwtUtils);

        // Выполняем фильтр
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Проверяем поведение
        verify(filterChain).doFilter(request, response);
    }

    @DisplayName("JWT-токен действителен. У пользовательского сервсиа запрашивается корректный пользователь")
    @Test
    void testDoFilterInternalValidTokenLoadingUser() throws ServletException, IOException {
        // Мокируем поведение
        lenient().when(jwtUtils.isTokenValid(JWT_TOKEN, userDetails)).thenReturn(true);
        jwtAuthFilter = new JwtAuthFilter(userDetailsService, jwtUtils);

        // Выполняем фильтр
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Проверяем поведение
        verify(userDetailsService).loadUserByUsername("username");
    }

    @DisplayName("JWT-токен действителен. Токен проверяется на валидность")
    @Test
    void testDoFilterInternalValidTokenCheck() throws ServletException, IOException {
        // Мокируем поведение
        when(this.jwtUtils.isTokenValid(JWT_TOKEN, userDetails))
                .thenReturn(true);
        jwtAuthFilter = new JwtAuthFilter(userDetailsService, jwtUtils);

        // Выполняем фильтр
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Проверяем поведение
        verify(jwtUtils).isTokenValid(JWT_TOKEN, userDetails);
    }
    @DisplayName("JWT-токен действителен. У запроса проверяются куки файлы")
    @Test
    void testDoFilterInternalValidToken() throws ServletException, IOException {
        // Мокируем поведение
        lenient()
                .when(jwtUtils.isTokenValid(JWT_TOKEN, userDetails))
                .thenReturn(true);
        jwtAuthFilter = new JwtAuthFilter(userDetailsService, jwtUtils);

        // Выполняем фильтр
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Проверяем поведение
        verify(request).getCookies();
    }

    @DisplayName("JWT-токен не действителен")
    @Test
    void testDoFilterInternalInValidToken() throws ServletException, IOException {
        // Мокируем поведение невалидного токена
        when(jwtUtils.isTokenValid(JWT_TOKEN, userDetails)).thenReturn(false);
        jwtAuthFilter = new JwtAuthFilter(userDetailsService, jwtUtils);

        // Выполняем фильтр
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Проверяем поведение
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService).loadUserByUsername("username");
        verify(jwtUtils).isTokenValid(JWT_TOKEN, userDetails);
        verify(request).getCookies();

        verify(response).addCookie(cookieCaptor.capture());
        Cookie addedCookie = cookieCaptor.getValue();
        Cookie expectedCookie = new Cookie("jwt", "");
        expectedCookie.setMaxAge(0);
        Assertions.assertEquals(expectedCookie, addedCookie, "Фильтр не сбрасывает куки клиента, если у клиента передали протухший токен");
    }
}