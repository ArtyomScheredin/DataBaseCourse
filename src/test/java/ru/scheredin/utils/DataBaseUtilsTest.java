package ru.scheredin.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.scheredin.utils.DataBaseUtils.ResultSetConverter;


import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("lera")
class DataBaseUtilsTest {
    private DataBaseUtils dataBaseUtils;
    private DataBaseUtils spyDataBaseUtils;

    @BeforeEach
    void setUp() {
        dataBaseUtils = new DataBaseUtils();
        spyDataBaseUtils = spy(dataBaseUtils);
    }

    @Test
    void querySingleReturnsSingleElement() {
        // Предполагаемый результат от query
        String expectedSingleResult = "TestResult";
        doReturn(Collections.singletonList(expectedSingleResult))
                .when(spyDataBaseUtils)
                .query(any(String.class), any(ResultSetConverter.class));

        // Вызов метода querySingle
        String actualResult = spyDataBaseUtils.querySingle("SELECT something FROM table WHERE condition", resultSet -> resultSet.getString("column"));

        // Проверка результата
        assertEquals(expectedSingleResult, actualResult, "Должен вернуться один элемент");
    }

    @Test
    void querySingleReturnsNullWhenNoElement() {
        // Предполагаемый результат  - пустой список
        doReturn(Collections.emptyList())
                .when(spyDataBaseUtils)
                .query(any(String.class), any(ResultSetConverter.class));

        // Вызов метода querySingle
        String actualResult = spyDataBaseUtils.querySingle("SELECT something FROM table WHERE condition", resultSet -> resultSet.getString("column"));

        // Проверка результата
        assertNull(actualResult, "null при отсутствии элементов");
    }
}
