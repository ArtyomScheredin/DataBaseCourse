package ru.scheredin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.scheredin.config.JwtAuthFilter;
import ru.scheredin.dto.Refund;
import ru.scheredin.services.RefundsService;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RefundsController.class)
@AutoConfigureMockMvc
class RefundsControllerTest {

    @MockBean
    private RefundsService refundsService;
    @MockBean
    private JwtAuthFilter jwtAuthFilter;
    @Mock
    private Principal principal = Mockito.mock(Principal.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private AutoCloseable autoCloseable;
    private RefundsController underTest;

    //ARGS
    public static final String LOGIN = "login";
    public static final String DESCRIPTION = "some info";
    public static final String DESCRIPTION_JSON = new Gson().toJson(new RefundsController.LocalDto(DESCRIPTION));
    public static final Integer REFUND_ID = 1;
    public static final Integer ORDER_ID = 2;
    public static final Refund refund = new Refund(1, 2, "some info", false, 4);
    public static final List<Refund> refundsList = List.of(refund);


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new RefundsController(refundsService, objectMapper);
        Mockito.when(principal.getName()).thenReturn(LOGIN);
        this.mockMvc = MockMvcBuilders.standaloneSetup(underTest).build();

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    //GET
    @Test
    void getAssignedRefundsSuccess() throws Exception {
        //give
        when(refundsService.getAssignedRefunds(any(String.class))).thenReturn(refundsList);
        String URL = "/orders/refund/assigned";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(URL)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(LOGIN));
        String expectedJson = "[{\"refund_id\":1,\"order_id\":2,\"description\":\"some info\"," +
                "\"approved\":false,\"employee_id\":4}]";
        //when
        MvcResult result = this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        String actualJson = result.getResponse().getContentAsString();
        assertEquals(expectedJson,actualJson);
        verify(refundsService, Mockito.times(1)).getAssignedRefunds(Mockito.eq(LOGIN));
    }

    @Test
    void getMyRefunds() throws Exception {
        //give
        when(refundsService.getMyRefunds(any(String.class))).thenReturn(refundsList);
        String URL = "/orders/refund/my";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(URL)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(LOGIN));
        String expectedJson = "[{\"refund_id\":1,\"order_id\":2,\"description\":\"some info\"," +
                "\"approved\":false,\"employee_id\":4}]";
        //when
        MvcResult result = this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        String actualJson = result.getResponse().getContentAsString();
        assertEquals(expectedJson,actualJson);
        verify(refundsService, Mockito.times(1)).getMyRefunds(Mockito.eq(LOGIN));
    }

    //POST
    @Test
    void requestRefundSuccess() throws Exception {
        //give
        when(refundsService.createRefund(any(Integer.class), any(String.class))).thenReturn(true);
        when(refundsService.isCouldBeRefunded(any(Integer.class))).thenReturn(true);
        when(refundsService.isOwner(any(String.class), any(Integer.class))).thenReturn(true);
        String URL = "/orders/{orderId}/refund";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URL, ORDER_ID)
                .content(DESCRIPTION_JSON)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(LOGIN));
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk());
        //then
        InOrder inOrder = Mockito.inOrder(refundsService);
        inOrder.verify(refundsService, Mockito.times(1)).isCouldBeRefunded(Mockito.eq(ORDER_ID));
        inOrder.verify(refundsService, Mockito.times(1)).isOwner(Mockito.eq(LOGIN),Mockito.eq(ORDER_ID));
        inOrder.verify(refundsService, Mockito.times(1)).createRefund(Mockito.eq(ORDER_ID),
                Mockito.eq(DESCRIPTION));
    }

    @Test
    void requestRefundOrderNotFound() throws Exception {
        //give
        when(refundsService.isCouldBeRefunded(any(Integer.class))).thenReturn(false);
        String URL = "/orders/{orderId}/refund";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URL, ORDER_ID)
                .content(DESCRIPTION_JSON)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(LOGIN));
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNotFound());
        //then
        InOrder inOrder = Mockito.inOrder(refundsService);
        inOrder.verify(refundsService, Mockito.times(1)).isCouldBeRefunded(Mockito.eq(ORDER_ID));
        inOrder.verify(refundsService, Mockito.never()).isOwner(any(),any());
        inOrder.verify(refundsService, Mockito.never()).createRefund(any(),any());
    }

    @Test
    void requestRefundNotOrderOwner() throws Exception {
        //give
        when(refundsService.isCouldBeRefunded(any(Integer.class))).thenReturn(true);
        when(refundsService.isOwner(any(String.class), any(Integer.class))).thenReturn(false);
        String URL = "/orders/{orderId}/refund";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URL, ORDER_ID)
                .content(DESCRIPTION_JSON)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(LOGIN));
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(401));
        //then
        InOrder inOrder = Mockito.inOrder(refundsService);
        inOrder.verify(refundsService, Mockito.times(1)).isCouldBeRefunded(Mockito.eq(ORDER_ID));
        inOrder.verify(refundsService, Mockito.times(1)).isOwner(Mockito.eq(LOGIN),Mockito.eq(ORDER_ID));
        inOrder.verify(refundsService, Mockito.never()).createRefund(any(),any());
    }

    @Test
    void requestRefundServiceFailure() throws Exception {
        //give
        when(refundsService.isCouldBeRefunded(any(Integer.class))).thenReturn(true);
        when(refundsService.isOwner(any(String.class), any(Integer.class))).thenReturn(true);
        when(refundsService.createRefund(any(Integer.class), any(String.class))).thenReturn(false);
        String URL = "/orders/{orderId}/refund";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URL, ORDER_ID)
                .content(DESCRIPTION_JSON)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(LOGIN));
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
        //then
        InOrder inOrder = Mockito.inOrder(refundsService);
        inOrder.verify(refundsService, Mockito.times(1)).isCouldBeRefunded(Mockito.eq(ORDER_ID));
        inOrder.verify(refundsService, Mockito.times(1)).isOwner(Mockito.eq(LOGIN),Mockito.eq(ORDER_ID));
        inOrder.verify(refundsService, Mockito.times(1)).createRefund(Mockito.eq(ORDER_ID),
                Mockito.eq(DESCRIPTION));
    }

    //PUT
    @Test
    void approveRefundSuccess() throws Exception {
        //give
        when(refundsService.approveRefund(any(Integer.class))).thenReturn(true);
        String URL = "/orders/refund/{refundId}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(URL, REFUND_ID)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(LOGIN));
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk());
        //then
        verify(refundsService, Mockito.times(1)).approveRefund(Mockito.eq(REFUND_ID));
    }

    @Test
    void approveRefundInvalidPathVariable() throws Exception {
        //give
        String URL = "/orders/refund/{refundId}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(URL, -1)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(LOGIN));
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
        //then
        verify(refundsService, Mockito.never()).approveRefund(any());
    }

    @Test
    void approveNotFoundRefund() throws Exception {
        //give
        when(refundsService.approveRefund(any(Integer.class))).thenReturn(false);
        String URL = "/orders/refund/{refundId}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(URL, REFUND_ID)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(SecurityMockMvcRequestPostProcessors.user(LOGIN));
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNotFound());
        //then
        verify(refundsService, Mockito.times(1)).approveRefund(Mockito.eq(REFUND_ID));
    }
}