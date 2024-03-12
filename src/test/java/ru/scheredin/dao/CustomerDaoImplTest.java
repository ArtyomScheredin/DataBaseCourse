package ru.scheredin.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.scheredin.utils.DataBaseUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("anna")
class CustomerDaoImplTest {

    @Mock
    private DataBaseUtils dataBaseUtils;
    private CustomerDaoImpl underTest;

    private AutoCloseable autoCloseable;

    //ARGS
    public static final Integer BALANCE = 100;
    public static final String LOGIN = "login";

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerDaoImpl(dataBaseUtils);
        when(dataBaseUtils.querySingle(any(String.class), any(DataBaseUtils.ResultSetConverter.class))).thenReturn(BALANCE);
        when(dataBaseUtils.execute(any(String.class))).thenReturn(1);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getBalance() {
        //give
        String exceptedQuery = String.format("select balance from customers join users u using(user_id) where login='%s';", LOGIN);
        //when
        Integer actualBalance = underTest.getBalance(LOGIN);
        //then
        assertEquals(BALANCE, actualBalance);
    }

    @Test
    void updateBalance() {
        //give
        String exceptedQuery = String.format("update customers set balance=%d where customers.user_id in" +
                " (select user_id from users where login='%s');",BALANCE, LOGIN);
        //when
        boolean isUpdated = underTest.updateBalance(LOGIN,BALANCE);
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).execute(queryCaptor.capture());
        assertTrue(isUpdated);
        assertEquals(exceptedQuery, queryCaptor.getValue());
    }
}