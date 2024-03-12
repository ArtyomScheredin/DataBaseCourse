package ru.scheredin.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.scheredin.dao.ProductsDao;
import ru.scheredin.dto.Product;
import ru.scheredin.utils.DataBaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("denis")
public class ProductsServiceTests {
    @Mock
    private ProductsDao productsDao;
    private DataBaseUtils dataBaseUtils;
    private ProductsService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new ProductsService(productsDao, dataBaseUtils);
    }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }
    @Test
    @DisplayName("Тест по выводу списка совподающих продуктов c фильтрами по имени продукта")
    void findAllMatchingProductsTest(){
        //give
        Map<String, String> filters = new HashMap<>();
        filters.put("name", "iphone");
        //when
        underTest.findAllMatching(filters);
        //then
        verify(productsDao).findAllProducts();
    }
    @Test
    @DisplayName("Тест по выводу списка совподающих продуктов c пустыми фильтрами")
    void findAllMatchingProductsTest1() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(0, "iphone", "phone"
                , 5000, 20, false));

        products.add(new Product(0, "iphone", "phone"
                , 5000, 20, false));

        when(productsDao.findAllProducts()).thenReturn(products);

        List<Product> result = underTest.findAllMatching(new HashMap<>());

        assertEquals(2, result.size());
    }
    @Test
    @DisplayName("Тест по выводу списка совподающих продуктов c фильтрами price_min/price_max")
    void findAllMatchingProductsTest2() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(0, "iphone", "phone"
                , 60, 20, false));

        products.add(new Product(0, "iphone", "phone"
                , 5000, 20, false));

        when(productsDao.findAllProducts()).thenReturn(products);
        Map<String, String> filters = new HashMap<>();
        filters.put("price_min", "50");
        filters.put("price_max", "100");

        List<Product> result = underTest.findAllMatching(filters);

        assertEquals(1, result.size());
    }
    @Test
    @DisplayName("Тест по выводу списка совподающих продуктов c фильтрами discontinued")
    void findAllMatchingProductsTest3() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(0, "iphone", "phone"
                , 60, 20, false));

        products.add(new Product(0, "iphone", "phone"
                , 5000, 20, true));

        when(productsDao.findAllProducts()).thenReturn(products);
        Map<String, String> filters = new HashMap<>();
        filters.put("discontinued", "true");

        List<Product> result = underTest.findAllMatching(filters);

        assertEquals(1, result.size());
    }
    @Test
    @DisplayName("Тест по выводу списка совподающих продуктов c фильтрами sort - ascending")
    void findAllMatchingProductsTest4() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(0, "iphone", "phone"
                , 60, 20, false));

        products.add(new Product(0, "iphone", "phone"
                , 5000, 20, true));

        when(productsDao.findAllProducts()).thenReturn(products);
        Map<String, String> filters = new HashMap<>();
        filters.put("sort", "ascending");

        List<Product> result = underTest.findAllMatching(filters);

        assertEquals(2, result.size());
        assertEquals(products, result);
    }
    @Test
    @DisplayName("Тест по выводу списка совподающих продуктов c фильтрами sort - not ascending")
    void findAllMatchingProductsTest5() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(0, "iphone", "phone"
                , 5000, 20, true));

        products.add(new Product(0, "iphone", "phone"
                , 3000, 20, true));

        products.add(new Product(0, "iphone", "phone"
                , 60, 20, false));

        when(productsDao.findAllProducts()).thenReturn(products);
        Map<String, String> filters = new HashMap<>();
        filters.put("sort", "not ascending");

        List<Product> result = underTest.findAllMatching(filters);

        assertEquals(3, result.size());
        assertEquals(products, result);
    }
    @Test
    @DisplayName("Тест по выводу списка совподающих продуктов c фильтрами quantity_min")
    void findAllMatchingProductsTest6() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(0, "iphone", "phone"
                , 5000, 10, true));

        products.add(new Product(0, "iphone", "phone"
                , 3000, 20, true));

        products.add(new Product(0, "iphone", "phone"
                , 60, 20, false));

        when(productsDao.findAllProducts()).thenReturn(products);
        Map<String, String> filters = new HashMap<>();
        filters.put("quantity_min", "10");

        List<Product> result = underTest.findAllMatching(filters);

        assertEquals(2, result.size());
    }
    @Test
    @DisplayName("Тест по поиску списка продуктов без скидки")
    void findAllProductsNotDiscontinuedTest(){
        //give
        //when
        underTest.findAllProductsNotDiscontinued();
        //then
        verify(productsDao).findAllProductsNotDiscontinued();
    }
}
