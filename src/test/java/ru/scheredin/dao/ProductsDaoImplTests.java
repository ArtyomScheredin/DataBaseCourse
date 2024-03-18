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
import ru.scheredin.dto.Product;
import ru.scheredin.utils.DataBaseUtils;

import java.util.List;

@Tag("denis")
public class ProductsDaoImplTests {
    @Mock
    private DataBaseUtils dataBaseUtils;
    @Autowired
    private ProductsDaoImpl underTest;
    private AutoCloseable autoCloseable;
    private UserDetails userDetails;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new ProductsDaoImpl(dataBaseUtils);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    @Test
    @DisplayName("Тест проверка")
    void findUserByLoginTest(){
        List<Product> res = underTest.findAllProducts();
        List<Integer> res1 = underTest.findAllProductsNotDiscontinued();
    }
}