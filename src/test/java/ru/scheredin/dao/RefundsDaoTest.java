package ru.scheredin.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.scheredin.dto.Refund;
import ru.scheredin.utils.DataBaseUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("anna")
class RefundsDaoTest {

    @Mock
    private DataBaseUtils dataBaseUtils;
    private RefundsDao underTest;

    private AutoCloseable autoCloseable;

    //ARGS
    public static final Integer REFUND_ID = 1;
    public static final Integer ORDER_ID = 2;
    public static final Integer EMPLOYEE_ID = 3;
    public static final String DESCRIPTION = "some info";
    public static final String LOGIN = "login";
    public static final Refund REFUND = new Refund(REFUND_ID,ORDER_ID,DESCRIPTION,false,EMPLOYEE_ID);
    public static final List<Refund> REFUNDS = new ArrayList<>(List.of(REFUND));

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new RefundsDao(dataBaseUtils);
        when(dataBaseUtils.query(any(String.class), any(Class.class))).thenReturn(REFUNDS);
        when(dataBaseUtils.execute(any(String.class))).thenReturn(1);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Test
    void getAll() {
        //give
        String exceptedQuery = "select * from refunds;";
        //when
        List<Refund> actualRefunds = underTest.getAll();
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).query(queryCaptor.capture(), Mockito.eq(Refund.class));
        assertEquals(REFUNDS, actualRefunds);
        assertEquals(exceptedQuery, queryCaptor.getValue());
    }

    @Test
    void approveRefund() {
        //give
        String exceptedQuery = String.format("update refunds set approved=true where refund_id=%d;", REFUND_ID);
        //when
        boolean isApproved = underTest.approveRefund(REFUND_ID);
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).execute(queryCaptor.capture());
        assertTrue(isApproved);
        assertEquals(exceptedQuery, queryCaptor.getValue());
    }

    @Test
    void createRefund() {
        //give
        String exceptedQuery = String.format("insert into refunds (order_id, description, approved, employee_id)\n" +
                "values (%d, '%s',false, %d);", ORDER_ID, DESCRIPTION, EMPLOYEE_ID);
        //when
        boolean isCreated = underTest.createRefund(ORDER_ID, DESCRIPTION, EMPLOYEE_ID);
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).execute(queryCaptor.capture());
        assertTrue(isCreated);
        assertEquals(exceptedQuery, queryCaptor.getValue());
    }


    @Test
    void findByCustomerLogin() {
        //give
        String exceptedQuery = String.format("""
            select * from refunds where order_id in 
            (select order_id from orders 
            join users u on u.user_id = orders.customer_id where login='%s');""", LOGIN);
        //when
        List<Refund> actualRefunds = underTest.findByCustomerLogin(LOGIN);
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).query(queryCaptor.capture(), Mockito.eq(Refund.class));
        assertEquals(exceptedQuery, queryCaptor.getValue());
        assertEquals(REFUNDS, actualRefunds);
    }

    @Test
    void findWithOrderId() {
        //give
        String exceptedQuery = String.format("select * from refunds where order_id=%d;", ORDER_ID);
        //when
        List<Refund> actualRefunds = underTest.findWithOrderId(ORDER_ID);
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).query(queryCaptor.capture(), Mockito.eq(Refund.class));
        assertEquals(exceptedQuery, queryCaptor.getValue());
        assertEquals(REFUNDS, actualRefunds);
    }

    @Test
    void findWithOrderIdAndCustomerLogin() {
        //give
        String exceptedQuery = String.format("""
            select * from refunds where order_id = %d and order_id in 
            (select order_id from orders 
            join users u on u.user_id = orders.customer_id where login='%s);""", ORDER_ID, LOGIN);
        //when
        List<Refund> actualRefunds = underTest.findWithOrderIdAndCustomerLogin(LOGIN,ORDER_ID);
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).query(queryCaptor.capture(), Mockito.eq(Refund.class));
        assertEquals(exceptedQuery, queryCaptor.getValue());
        assertEquals(REFUNDS, actualRefunds);
    }

    @Test
    void findByEmployeeLogin() {
        //give
        String exceptedQuery = "select * from refunds;";
        //when
        List<Refund> actualRefunds = underTest.findByEmployeeLogin(LOGIN);
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).query(queryCaptor.capture(), Mockito.eq(Refund.class));
        assertEquals(exceptedQuery, queryCaptor.getValue());
        assertEquals(REFUNDS, actualRefunds);
    }
}