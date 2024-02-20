package ru.scheredin.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {
    public static final int DELTA_TO_COMPARE_DATES_MS = 10;
    @Value("${jwt.expiration.hours}")
    private int JWT_EXPIRATION_HOURS = 0;
    public static final String TEST_USER = "testUser";
    public static final String AUTHORITIES = "authorities";
    public static final String SECRET = "secret";
    public static final Date EXPIRATION_DATE_OF_EXPIRED_TOKEN = new Date(System.currentTimeMillis() - 1000);
    private static final String EXPIRED_TOKEN = Jwts.builder()
            .setSubject(TEST_USER)
            .claim(AUTHORITIES, Collections.emptyList())
            .setExpiration(EXPIRATION_DATE_OF_EXPIRED_TOKEN) // Expired token
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET)
            .compact();

    public static final Date EXPRITATION_DATE = new Date(System.currentTimeMillis() + 100000);
    private static final String TOKEN = Jwts.builder()
            .setSubject(TEST_USER)
            .claim(AUTHORITIES, Collections.emptyList())
            .setExpiration(EXPRITATION_DATE) //Non-expired token
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET)
            .compact();
    private final JwtUtils jwtUtils = new JwtUtils("secret", 8);
    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(userDetails.getUsername()).thenReturn(TEST_USER);
        Mockito.lenient().when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
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
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtils.extractUsername(EXPIRED_TOKEN));
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
        assertEquals(EXPRITATION_DATE.getTime(), expiration.getTime(), DELTA_TO_COMPARE_DATES_MS, "Даты не совпадают");
    }

    @DisplayName("Извлечение времени истечения из протухшевго токена")
    @Test
    public void testExtractExpirationFromExpiredToken() {
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtils.extractExpiration(EXPIRED_TOKEN));
    }

    @DisplayName("Проверка наличия утверждений в токене")
    @Test
    public void testHasClaims() {
        boolean hasClaims = jwtUtils.hasClaims(TOKEN, AUTHORITIES);
        assertTrue(hasClaims);
    }

    @DisplayName("Проверка наличия утверждений в просроченном токене")
    @Test
    public void testExpiredHasClaims() {
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtils.hasClaims(EXPIRED_TOKEN, AUTHORITIES));
    }

    @DisplayName("Проверка наличия утверждений в токене")
    @Test
    public void testHasClaimsOfExpiredToken() {
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtils.hasClaims(EXPIRED_TOKEN, AUTHORITIES));
    }

    @DisplayName("Проверка того, истек ли срок действия токена")
    @Test
    public void testIsTokenExpired() {
        Boolean isExpired = jwtUtils.isTokenExpired(EXPIRED_TOKEN);
        assertTrue(isExpired);
    }

    @DisplayName("Извлечение утверждения из просроченного токена")
    @Test
    public void testExtractClaim() {
        String username = jwtUtils.extractClaim(TOKEN, Claims::getSubject);
        assertEquals(TEST_USER, username);
    }

    @DisplayName("Извлечение утверждения из токена")
    @Test
    public void testExtractExpiredClaim() {
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtils.extractClaim(EXPIRED_TOKEN, Claims::getSubject));
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

    @DisplayName("Генерация токена с утверждениями")
    @ParameterizedTest
    @MethodSource("getClaims")
    public void testGenerateTokenWithClaims(Map<String, Object> claims) {
        String token = jwtUtils.generateToken(userDetails, claims);
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            Object claim = jwtUtils.extractClaim(token, getClaimsFunction(entry));

            Assertions.assertEquals(entry.getValue(), claim, "Утверждениями не совпало");
        }
    }

    private static Function<Claims, ?> getClaimsFunction(Map.Entry<String, Object> entry) {
        return claimsRaw -> claimsRaw.get(entry.getKey(), entry.getValue().getClass());
    }

    private static Stream<Arguments> getClaims() {
        return Stream.of(
                Arguments.of(Collections.emptyMap()),
                Arguments.of(Map.of("claim1", "value")),
                Arguments.of(Map.of("claim2", "value2")),
                Arguments.of(Map.of("claim2", "value2", "claim3", "value4"))
        );
    }

    @DisplayName("Проверка валидности токена")
    @Test
    public void testIsTokenValid() {
        assertTrue(jwtUtils.isTokenValid(TOKEN, userDetails));
    }

    @DisplayName("Проверка валидности протухшего токена")
    @Test
    public void testIsExpiredTokenValid() {
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtils.isTokenValid(EXPIRED_TOKEN, userDetails));
    }

}