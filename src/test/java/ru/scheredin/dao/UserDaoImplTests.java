package ru.scheredin.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import ru.scheredin.utils.DataBaseUtils;

@Tag("denis")
public class UserDaoImplTests {
    @Mock
    private DataBaseUtils dataBaseUtils;
    @Autowired
    private UserDaoImpl underTest;
    private AutoCloseable autoCloseable;
    private UserDetails userDetails;

    private static final String LOGIN = "LOGIN";
    public static final String SELECT_ALL_USERS = """
            select * from get_user_info_by_name('%s');""";
    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new UserDaoImpl(dataBaseUtils);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    @Test
    @DisplayName("Тест проверка")
    void findUserByLoginTest(){
        UserDetails res = underTest.findUserByLogin(LOGIN);
    }
    @Test
    @DisplayName("Тест проверка")
    void findUserIdByLogin(){
        Integer res = underTest.findUserIdByLogin(LOGIN);
    }
}
