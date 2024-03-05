package ru.scheredin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.scheredin.dto.Order;
import ru.scheredin.services.CustomerService;
import ru.scheredin.services.OrdersService;
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

public class OrdersControllerTests {
    @Mock
    private OrdersService ordersService;
    @Mock
    private Principal principal = Mockito.mock(Principal.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private List<Order> orders;
    private AutoCloseable autoCloseable;
    private OrdersController underTest;
    public static final Map<Integer, Integer> map = new HashMap<>();
    public static final String DESCRIPTION_JSON = new Gson().toJson(map);

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new OrdersController(ordersService, objectMapper);
        this.mockMvc = MockMvcBuilders.standaloneSetup(underTest).build();

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    @Test
    @DisplayName("Тест создания заказа")
    void createOrderTest() throws Exception {
        when(ordersService.createOrder(map, principal.getName())).thenReturn(0);

        ResponseEntity<Integer> resp = underTest.createOrder(map, principal);

        assertEquals(ResponseEntity.accepted().body(0), resp);
    }
    @Test
    @DisplayName("Тест создания заказа, если не найден orderId")
    void createOrderTest1() throws Exception {
        when(ordersService.createOrder(map, principal.getName())).thenReturn(null);

        ResponseEntity<Integer> resp = underTest.createOrder(map, principal);

        assertEquals(ResponseEntity.badRequest().build(), resp);
    }
    @Test
    @DisplayName("Тест создания заказа, если principal == null")
    void createOrderTest2() throws Exception {
        ResponseEntity<Integer> resp = underTest.createOrder(map, null);

        assertEquals(ResponseEntity.badRequest().build(), resp);
    }

    @Test
    @DisplayName("Тест для получения моих заказов")
    void getMyOrdersTest() throws Exception {
        when(ordersService.getOrders(principal.getName())).thenReturn(orders);

        ResponseEntity<String> resp = underTest.getMyOrders(principal);

        assertEquals(ResponseEntity.ok(objectMapper.writeValueAsString(orders)), resp);
    }
    @Test
    @DisplayName("Тест для получения моих заказов, если principal == null")
    void getMyOrdersTest1()throws Exception{

        ResponseEntity<String> resp = underTest.getMyOrders(null);

        assertEquals(ResponseEntity.badRequest().build(), resp);
    }
}