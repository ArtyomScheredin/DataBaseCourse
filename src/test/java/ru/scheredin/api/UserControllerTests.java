package ru.scheredin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.scheredin.services.CustomerService;
import ru.scheredin.utils.DataBaseUtils;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTests {
    @Mock
    private CustomerService customerService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private DataBaseUtils dataBaseUtils;
    @Mock
    private Principal principal = Mockito.mock(Principal.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private AutoCloseable autoCloseable;
    private UserController underTest;

    public static final Integer USER_ID = 2;
    public static final Boolean BANNED = true;
    public static final UserController.PersonDto DTO_PERSON = new UserController.PersonDto();
    public static final String DESCRIPTION_JSON = new Gson().toJson(BANNED);
    public static final String DTO_PERSON_JSON = new Gson().toJson(DTO_PERSON);
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new UserController(customerService, userDetailsService, dataBaseUtils, objectMapper);
        this.mockMvc = MockMvcBuilders.standaloneSetup(underTest).build();

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    @Test
    @DisplayName("Тест блокировки пользователя если Principal null")
    void blockUserPrincipalNullTest() throws Exception {
        ResponseEntity<String> response = underTest.blockUser(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    @DisplayName("Тест блокировки пользователя если Principal not null")
    void blockUserPrincipalNotNullTest() throws Exception {
        // Arrange
        UserDetails userDetails = new User("testUser", "password", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailsService.loadUserByUsername(principal.getName())).thenReturn(userDetails);

        // Act
        ResponseEntity<String> response = underTest.blockUser(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String expectedResponse = "\"name\":\"testUser\",\"role\":\"USER\""; // Make sure to update this string if your code logic changes

        String[] parts = expectedResponse.split(",");
        Boolean test1 = response.getBody().contains(parts[0]);
        Boolean test2 = response.getBody().contains(parts[1]);

        assertEquals(true, test1);
        assertEquals(true, test2);
        //assertEquals(expectedResponse, response.getBody());
    }
    @Test
    @DisplayName("Тест блокировки пользователя по айди")
    void blockUserByIdTest() throws Exception {
        UserDetails userDetails = new User("testUser", "password", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        when(dataBaseUtils.execute(String.format("""
                                                    update users set blocked=%b where user_id=%d;""", true, USER_ID))).thenReturn(0);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/{user_id}/ban", USER_ID)
                .principal(principal)
                .content(DESCRIPTION_JSON)
                .param("banned", String.valueOf(BANNED))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails));

        MvcResult result = this.mockMvc
                .perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
    @Test
    @DisplayName("Тест обновления зарплаты на положительное число")
    void changeSalaryTest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/{user_id}/salary", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("newSalary", "50000");
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Тест обновления зарплаты на отрицательное число")
    void changeSalaryTest1() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/{user_id}/salary", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("newSalary", "-50000");
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    @DisplayName("Тест обновления зарплаты, если не передали число")
    void changeSalaryTest2() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/{user_id}/salary", USER_ID)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    @DisplayName("Тест получения баланса")
    void getBalanceTest() throws Exception {
        when(customerService.getBalance(principal.getName())).thenReturn(1000); // Заменяем реальный вызов сервиса заглушкой

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/balance")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1000"));
    }
    @Test
    @DisplayName("Тест получения баланса при отсутствии principal")
    void getBalanceTest1() throws Exception {
        ResponseEntity<Integer> resp = underTest.getBalance(null);
        assertEquals(ResponseEntity.badRequest().build(), resp);
    }
    @Test
    @DisplayName("Тест обновления баланса")
    void updateBalanceTest() throws Exception {
        when(customerService.updateBalance(principal.getName(), -100)).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/balance")
                .principal(principal)
                .param("newBalance", "-100")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    @DisplayName("Тест обновления баланса при отрицательном customerService.updateBalance")
    void updateBalanceTest1() throws Exception {
        when(customerService.updateBalance(principal.getName(), -100)).thenReturn(false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/balance")
                .principal(principal)
                .param("newBalance", "-100")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    @DisplayName("Тест получения пользователей")
    void getPeopleTest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/people")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    @DisplayName("Тест сохранения пользователя")
    void saveCustomerTest() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/customer/")
                .content(DTO_PERSON_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
}
