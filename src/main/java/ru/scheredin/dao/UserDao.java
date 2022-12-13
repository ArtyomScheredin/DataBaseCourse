package ru.scheredin.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import ru.scheredin.utils.DataBaseUtils;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDao {

    private final DataBaseUtils dataBaseUtils;

    public static final String SELECT_ALL_USERS = """
            select * from get_user_info_by_name('%s');""";

    public UserDetails findUserByLogin(String login) {
        return dataBaseUtils.querySingle(String.format(SELECT_ALL_USERS, login), getUserResultSetConverter());
    }

    public Integer findUserIdByLogin(String login) {
        return dataBaseUtils.querySingle(String.format("select user_id from users where login='%s';", login), r -> r.getInt("user_id"));
    }


    private static DataBaseUtils.ResultSetConverter<User> getUserResultSetConverter() {
        return resultSet -> resultSet.getString("blocked").equals("true") ? null : new User(
                resultSet.getString("login"), resultSet.getString("password"),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + resultSet.getString("role"))));
    }
}
