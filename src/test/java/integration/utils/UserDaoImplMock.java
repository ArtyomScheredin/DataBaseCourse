package integration.utils;

import org.springframework.security.core.userdetails.UserDetails;
import ru.scheredin.dao.UserDao;

import java.util.Objects;

public class UserDaoImplMock implements UserDao {
    @Override
    public UserDetails findUserByLogin(String login) {
        return null;
    }

    @Override
    public Integer findUserIdByLogin(String login) {
        if(Objects.equals(login, "Test1"))
            return 1;
        if(Objects.equals(login, "Test2"))
            return 2;
        return null;
    }
}
