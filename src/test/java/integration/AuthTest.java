package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.scheredin.DemoApplication;
import ru.scheredin.api.AuthenticationController;
import ru.scheredin.config.JwtAuthFilter;
import ru.scheredin.dao.UserDao;
import ru.scheredin.dto.AuthenticationRequest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Tags({
        @Tag("integration"),
        @Tag("artyom")
})
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthTest {
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    @Autowired
    AuthenticationController authenticationController;

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @MockBean
    UserDao userDao;
    @Autowired
    TestRestTemplate template;

    User user = new User(LOGIN,
            PASSWORD,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
    );
    @BeforeEach
    void setup() {
        Mockito.when(userDao.findUserByLogin(LOGIN)).thenReturn(user);
    }

    @Test
    @DisplayName("Генерация jwt токена через AuthenticationController и валидация этого токена в JwtAuthFilter")
    void authIntegrationTest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(LOGIN, PASSWORD);

        ResponseEntity<String> httpResponse = template.postForEntity( "/auth", authenticationRequest, String.class);

        assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpResponse.getBody()).isNotBlank();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "jwt=" + httpResponse.getBody());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> forEntity = template.exchange("/whoami", HttpMethod.GET, entity, String.class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(forEntity.getBody()).contains("USER");
    }
}
