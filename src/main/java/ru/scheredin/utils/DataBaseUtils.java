package ru.scheredin.utils;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
@NoArgsConstructor
public class DataBaseUtils {
    @Value("${datasource.url}")
    private final String url = null;
    @Value("${datasource.username}")
    private final String username = null;

    private Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", username);

        return DriverManager.getConnection(url, props);
    }

    public <T> List<T> query(String statement, ResultSetConverter<T> converter) {
        try (Connection conn = getConnection();
             Statement sqlStatement = conn.createStatement()) {
            ResultSet resultSet = sqlStatement.executeQuery(statement);
            ArrayList<T> list = new ArrayList<>();
            while (resultSet.next()) {
                T element = converter.convert(resultSet);
                list.add(element);
            }
            return list;
        } catch (Exception e) {
            System.err.println("Чёт пошло не так c запросами в базу\n" + e);
            throw new RuntimeException("Чёт пошло не так c запросами в базу" + e);
        }
    }

    public <T> List<T> query(String statement, Class<T> clazz) {
        return query(statement, resultSet -> {
            Field[] declaredFields = clazz.getDeclaredFields();
            T result = clazz.getConstructor().newInstance();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(Integer.class) || field.getType() == int.class) {
                    field.set(result, resultSet.getInt(field.getName()));
                }
                if (field.getType().isAssignableFrom(String.class)) {
                    field.set(result, resultSet.getString(field.getName()));
                }
                if (field.getType() == boolean.class) {
                    field.set(result, "t".equals(resultSet.getString(field.getName())));
                }
            }
            return result;
        });
    }

    public int update(String statement) {
        try (Connection conn = getConnection();
             Statement sqlStatement = conn.createStatement()) {
            return sqlStatement.executeUpdate(statement);

        } catch (SQLException e) {
            System.err.println("Чёт пошло не так c запросами в базу\n" + e);
            throw new RuntimeException("Чёт пошло не так c запросами в базу\n" + e);
        }
    }


    public interface ResultSetConverter<R> {
        R convert(ResultSet t) throws SQLException, Exception;
    }

}
