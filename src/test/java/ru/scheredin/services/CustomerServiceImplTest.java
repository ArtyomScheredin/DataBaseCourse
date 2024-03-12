package ru.scheredin.services;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.scheredin.dao.CustomerDaoImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("anna")
class CustomerServiceImplTest {

    //DEPENDENCIES
    @Mock
    private CustomerDaoImpl customerDaoImpl;

    private AutoCloseable autoCloseable;

    private CustomerServiceImpl underTest;

    //ARGS
    public static final String LOGIN = "login";
    public static final Integer BALANCE = 123;



    //PREPARE
    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerServiceImpl(customerDaoImpl);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    //GET
    @Test
    void getBalance() {
        //give
        when(customerDaoImpl.getBalance(Mockito.eq(LOGIN))).thenReturn(BALANCE);
        //when
        Integer getBalance = underTest.getBalance(LOGIN);
        //then
        assertEquals(BALANCE, getBalance);
        verify(customerDaoImpl, Mockito.times(1)).getBalance(LOGIN);
    }

    @Test
    void getBalanceNullLogin() {
        //give
        String expectedMessage = "Wrong login";
        //when
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            underTest.getBalance(null);
        });
        //then
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(customerDaoImpl, Mockito.never()).updateBalance(any(), any());
    }

    @Test
    void getBalanceBlankLogin() {
        //give
        String expectedMessage = "Wrong login";
        //when
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            underTest.getBalance("");
        });
        //then
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(customerDaoImpl, Mockito.never()).updateBalance(any(), any());
    }

    //CREATE
    @Test
    void updateBalanceNullLogin() {
        //give
        String expectedMessage = "Wrong login";
        //when
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            underTest.updateBalance(null, BALANCE);
        });
        //then
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(customerDaoImpl, Mockito.never()).updateBalance(any(), any());
    }

    @Test
    void updateBalanceBlankLogin() {
        //give
        String expectedMessage = "Wrong login";
        //when
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            underTest.updateBalance("", BALANCE);
        });
        //then
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(customerDaoImpl, Mockito.never()).updateBalance(any(), any());
    }

    @Test
    void updateBalanceNegativeBalance() {
        //give
        String expectedMessage = "Negative balance";
        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.updateBalance(LOGIN, -BALANCE);
        });
        //then
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(customerDaoImpl, Mockito.never()).updateBalance(any(), any());
    }

    @Test
    void updateBalanceExistsLogin() {
        //give
        when(customerDaoImpl.updateBalance(Mockito.eq(LOGIN), any(Integer.class))).thenReturn(true);
        //when
        boolean updated =  underTest.updateBalance(LOGIN, BALANCE);
        //then
        assertTrue(updated);
        verify(customerDaoImpl, Mockito.times(1)).updateBalance(LOGIN, BALANCE);
    }

    @Test
    void updateBalanceAbsenceLogin() {
        //give
        when(customerDaoImpl.updateBalance(Mockito.eq(LOGIN), any(Integer.class))).thenReturn(false);
        //when
        boolean updated =  underTest.updateBalance(LOGIN, BALANCE);
        //then
        assertDoesNotThrow(() -> customerDaoImpl.updateBalance(LOGIN, BALANCE));
        assertFalse(updated);
    }

}