package ru.scheredin.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "src/test/resources/application.properties")
@DisplayName("JwtUtils Класс тестов")
class JwtUtilsTest {
    @Value("${jwt.expiration.hours}")
    private int JWT_EXPIRATION_HOURS = 0;
    public static final String TEST_USER = "testUser";
    public static final String AUTHORITIES = "authorities";
    public static final String SECRET = "secret";
    public static final Date EXPIRATION_DATE_OF_EXPIRED_TOKEN = new Date(System.currentTimeMillis() - 1000);
    private static String EXPIRED_TOKEN = Jwts.builder()
            .setSubject(TEST_USER)
            .claim(AUTHORITIES, Collections.emptyList())
            .setExpiration(EXPIRATION_DATE_OF_EXPIRED_TOKEN) // Expired token
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET)
            .compact();

    public static final Date EXPRITATION_DATE = new Date(System.currentTimeMillis() + 1000);
    private static String TOKEN = Jwts.builder()
            .setSubject(TEST_USER)
            .claim(AUTHORITIES, Collections.emptyList())
            .setExpiration(EXPRITATION_DATE) //Non-expired token
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET)
            .compact();
    @Autowired
    private JwtUtils jwtUtils;
    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Mockito.when(userDetails.getUsername()).thenReturn(TEST_USER);
    }

    @DisplayName("Извлечение имени пользователя из токена")
    @Test
    public void testExtractUsername() {
        String username = jwtUtils.extractUsername(TOKEN);
        assertEquals(TEST_USER, username);
    }

    @DisplayName("Извлечение имени пользователя из просроченного токена")
    @Test
    public void testExtractUsernameFromExpiredToken() {
        String username = jwtUtils.extractUsername(EXPIRED_TOKEN);
        assertEquals(TEST_USER, username);
    }

    @DisplayName("Извлечение времени истечения из токена")
    @Test
    public void testExtractExpirationNonNull() {
        Date expiration = jwtUtils.extractExpiration(TOKEN);
        assertNotNull(expiration);
    }

    @DisplayName("Извлечение времени истечения из токена")
    @Test
    public void testExtractExpiration() {
        Date expiration = jwtUtils.extractExpiration(TOKEN);
        assertEquals(EXPRITATION_DATE, expiration);
    }

    @DisplayName("Проверка наличия утверждений в токене")
    @Test
    public void testHasClaims() {
        boolean hasClaims = jwtUtils.hasClaims(TOKEN, AUTHORITIES);
        assertTrue(hasClaims);
    }

    @DisplayName("Проверка наличия утверждений в токене")
    @Test
    public void testHasClaimsOfExpiredToken() {
        jwtUtils.hasClaims(EXPIRED_TOKEN, AUTHORITIES);
    }

    @DisplayName("Проверка того, истек ли срок действия токена")
    @Test
    public void testIsTokenExpired() {
        Boolean isExpired = jwtUtils.isTokenExpired(EXPIRED_TOKEN);
        assertTrue(isExpired);
    }

    @DisplayName("Извлечение утверждения из токена")
    @Test
    public void testExtractClaim() {
        String username = jwtUtils.extractClaim(TOKEN, Claims::getSubject);
        assertEquals(TEST_USER, username);
    }

    @DisplayName("Генерация токена без утверждений")
    @Test
    public void testGenerateToken() {
        String token = jwtUtils.generateToken(userDetails);
        assertNotNull(token);
    }

    @DisplayName("Генерация токена без утверждений. Проверка даты протухания")
    @Test
    public void testGenerateTokenCheckExpiration() {
        String token = jwtUtils.generateToken(userDetails);
        Date expirationDate = jwtUtils.extractExpiration(token);
        Date dateToCompare = Date.from(Instant.now().plus(JWT_EXPIRATION_HOURS - 1, ChronoUnit.HOURS));

        Assertions.assertTrue(expirationDate.after(dateToCompare), "Токен протухнет слишком рано");
    }

    @DisplayName("Генерация токена с утверждениями")
    @Test
    public void testGenerateTokenWithClaims() {
        Map<String, Object> claims = Collections.singletonMap("key", "value");
        String token = jwtUtils.generateToken(userDetails, claims);
        assertNotNull(token);
    }

    @DisplayName("Проверка валидности токена")
    @Test
    public void testIsTokenValid() {
        JwtUtils jwtUtils = new JwtUtils();
        Mockito.when(userDetails.getUsername()).thenReturn(TEST_USER);
        assertTrue(jwtUtils.isTokenValid(TOKEN, userDetails));
    }

    @DisplayName("Проверка валидности протухшего токена")
    @Test
    public void testIsExpiredTokenValid() {
        JwtUtils jwtUtils = new JwtUtils();
        Mockito.when(userDetails.getUsername()).thenReturn(TEST_USER);
        assertTrue(jwtUtils.isTokenValid(TOKEN, userDetails));
    }

}