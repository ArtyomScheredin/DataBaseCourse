package ru.scheredin.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.scheredin.dao.RefundsDao;
import ru.scheredin.dto.Employee;
import ru.scheredin.dto.Refund;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefundsServiceTest {
    //DEPENDENCIES
    @Mock
    private RefundsDao refundsDao;
    @Mock
    private EmployeesService employeesService;

    private AutoCloseable autoCloseable;

    private RefundsService underTest;

    //ARGS
    public static final String DESCRIPTION = "some info";
    public static final String LOGIN = "login";
    public static final Integer ORDER_ID = 1;
    public static final Integer EMPLOYEE_ID = 2;
    public static final Integer REFUND_ID = 3;
    public static final List<Refund> REFUNDS = new ArrayList<>(List.of(new Refund()));

    //PREPARE
    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new RefundsService(refundsDao, employeesService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    //CREATE
    //employee null
    @Test
    void createRefundNoEmployee() {
        //give
        when(refundsDao.findWithOrderId(any())).thenReturn(REFUNDS);
        when(employeesService.getRandomEmployee()).thenReturn(null);
        //when
        boolean created = underTest.createRefund(ORDER_ID, DESCRIPTION);
        //then
        assertFalse(created);
        verify(refundsDao, Mockito.never()).createRefund(any(), any(), any());
        verify(refundsDao, Mockito.times(1)).findWithOrderId(any());
        assertDoesNotThrow(() -> underTest.createRefund(ORDER_ID, DESCRIPTION));
    }

    //order do not exist
    @Test
    void createRefundNoOrder() {
        //give
        when(refundsDao.findWithOrderId(any())).thenReturn(Collections.EMPTY_LIST);
        //when
        boolean created = underTest.createRefund(ORDER_ID, DESCRIPTION);
        //then
        assertFalse(created);
        verify(refundsDao, Mockito.never()).createRefund(any(), any(), any());
        verify(refundsDao, Mockito.times(1)).findWithOrderId(any());
        assertDoesNotThrow(() -> underTest.createRefund(ORDER_ID, DESCRIPTION));
    }
    //description empty
    @Test
    void createRefundEmptyDescription() {
        //give
        //when
        boolean created = underTest.createRefund(ORDER_ID, "");
        //then
        assertFalse(created);
        verify(refundsDao, Mockito.never()).createRefund(any(), any(), any());
        assertDoesNotThrow(() -> underTest.createRefund(ORDER_ID, ""));
    }
    //success
    @Test
    void createRefundSuccess() {
        //give
        Employee employee = new Employee();
        employee.setUser_id(EMPLOYEE_ID);
        when(refundsDao.findWithOrderId(any())).thenReturn(REFUNDS);
        when(employeesService.getRandomEmployee()).thenReturn(employee);
        when(refundsDao.createRefund(any(), any(), any())).thenReturn(true);
        //when
        boolean created = underTest.createRefund(ORDER_ID, DESCRIPTION);
        //then
        assertTrue(created);
        verify(refundsDao, Mockito.times(1))
                .createRefund(ORDER_ID, DESCRIPTION, EMPLOYEE_ID);

    }
    //GET
    //success customer refunds
    @Test
    void getMyRefunds() {
        //give
        when(refundsDao.findByCustomerLogin(LOGIN)).thenReturn(REFUNDS);
        //when
        List<Refund> myRefunds = underTest.getMyRefunds(LOGIN);
        //then
        assertEquals(REFUNDS, myRefunds);
        verify(refundsDao, Mockito.times(1)).findByCustomerLogin(LOGIN);
    }

    //success employee refunds
    @Test
    void getAssignedRefunds() {
        //give
        when(refundsDao.findByEmployeeLogin(LOGIN)).thenReturn(REFUNDS);
        //when
        List<Refund> myRefunds = underTest.getAssignedRefunds(LOGIN);
        //then
        assertEquals(REFUNDS, myRefunds);
        verify(refundsDao, Mockito.times(1)).findByEmployeeLogin(LOGIN);
    }

    //APPROVE
    //refund not exists
    @Test
    void approveRefundNotExistsId() {
        //give
        REFUNDS.clear();
        Refund refund = new Refund();
        refund.setRefund_id(REFUND_ID+2);
        REFUNDS.add(refund);
        Refund anotherRefund = new Refund();
        refund.setRefund_id(REFUND_ID+1);
        REFUNDS.add(anotherRefund);
        when(refundsDao.getAll()).thenReturn(REFUNDS);
        //when
        boolean approved = underTest.approveRefund(REFUND_ID);
        //then
        assertFalse(approved);
        verify(refundsDao, Mockito.times(1)).getAll();
        verify(refundsDao, Mockito.never()).approveRefund(any());
        assertDoesNotThrow(() -> underTest.approveRefund(REFUND_ID));
    }
    //success
    @Test
    void approveRefundSuccess() {
        //give
        REFUNDS.clear();
        Refund refund = new Refund();
        refund.setRefund_id(REFUND_ID);
        REFUNDS.add(refund);
        Refund anotherRefund = new Refund();
        anotherRefund.setRefund_id(REFUND_ID+1);
        REFUNDS.add(anotherRefund);
        when(refundsDao.getAll()).thenReturn(REFUNDS);
        when(refundsDao.approveRefund(REFUND_ID)).thenReturn(true);
        //when
        boolean approved = underTest.approveRefund(REFUND_ID);
        //then
        assertTrue(approved);
        verify(refundsDao, Mockito.times(1)).getAll();
        verify(refundsDao, Mockito.times(1)).approveRefund(REFUND_ID);
    }


    @Test
    void isCouldBeRefundedFalse() {
        //give
        when(refundsDao.findWithOrderId(ORDER_ID)).thenReturn(REFUNDS);
        //when
        boolean isCouldBeRefunded = underTest.isCouldBeRefunded(ORDER_ID);
        //then
        assertFalse(isCouldBeRefunded);
        verify(refundsDao, Mockito.times(1)).findWithOrderId(ORDER_ID);
    }

    @Test
    void isCouldBeRefundedTrue() {
        //give
        when(refundsDao.findWithOrderId(ORDER_ID)).thenReturn(Collections.EMPTY_LIST);
        //when
        boolean isCouldBeRefunded = underTest.isCouldBeRefunded(ORDER_ID);
        //then
        assertTrue(isCouldBeRefunded);
        verify(refundsDao, Mockito.times(1)).findWithOrderId(ORDER_ID);
    }

    @Test
    void isOwnerTrue() {
        //give
        REFUNDS.clear();
        Refund refund = new Refund();
        refund.setOrder_id(ORDER_ID);
        REFUNDS.add(refund);
        Refund anotherRefund = new Refund();
        anotherRefund.setOrder_id(ORDER_ID+1);
        REFUNDS.add(anotherRefund);
        when(refundsDao.findByCustomerLogin(LOGIN)).thenReturn(REFUNDS);
        //when
        boolean isCouldBeRefunded = underTest.isOwner(LOGIN, ORDER_ID);
        //then
        assertTrue(isCouldBeRefunded);
        verify(refundsDao, Mockito.times(1)).findByCustomerLogin(LOGIN);
    }

    @Test
    void isOwnerFalse() {
        //give
        REFUNDS.clear();
        Refund refund = new Refund();
        refund.setOrder_id(ORDER_ID+1);
        REFUNDS.add(refund);
        Refund anotherRefund = new Refund();
        anotherRefund.setOrder_id(ORDER_ID+2);
        REFUNDS.add(anotherRefund);
        when(refundsDao.findByCustomerLogin(LOGIN)).thenReturn(REFUNDS);
        //when
        boolean isCouldBeRefunded = underTest.isOwner(LOGIN, ORDER_ID);
        //then
        assertFalse(isCouldBeRefunded);
        verify(refundsDao, Mockito.times(1)).findByCustomerLogin(LOGIN);
    }

    @Test
    void isAssignedEmployeeTrue() {
        //give
        REFUNDS.clear();
        Refund refund = new Refund();
        refund.setRefund_id(REFUND_ID);
        REFUNDS.add(refund);
        Refund anotherRefund = new Refund();
        anotherRefund.setRefund_id(REFUND_ID+1);
        REFUNDS.add(anotherRefund);
        when(refundsDao.findByCustomerLogin(LOGIN)).thenReturn(REFUNDS);
        //when
        boolean isAssignedEmployee = underTest.isAssignedEmployee(LOGIN, REFUND_ID);
        //then
        assertTrue(isAssignedEmployee);
        verify(refundsDao, Mockito.times(1)).findByCustomerLogin(LOGIN);
    }

    @Test
    void isAssignedEmployeeFalse() {
        //give
        REFUNDS.clear();
        Refund refund = new Refund();
        refund.setRefund_id(REFUND_ID+1);
        REFUNDS.add(refund);
        Refund anotherRefund = new Refund();
        anotherRefund.setRefund_id(REFUND_ID+2);
        REFUNDS.add(anotherRefund);
        when(refundsDao.findByCustomerLogin(LOGIN)).thenReturn(REFUNDS);
        //when
        boolean isAssignedEmployee = underTest.isAssignedEmployee(LOGIN, REFUND_ID);
        //then
        assertFalse(isAssignedEmployee);
        verify(refundsDao, Mockito.times(1)).findByCustomerLogin(LOGIN);
    }
}