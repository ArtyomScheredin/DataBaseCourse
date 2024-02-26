package ru.scheredin.config.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.scheredin.api.ProductsController;
import ru.scheredin.dto.Category;
import ru.scheredin.dto.Product;
import ru.scheredin.dto.Review;
import ru.scheredin.services.ProductsService;
import ru.scheredin.utils.DataBaseUtils;
import ru.scheredin.dao.UserDao;
import ru.scheredin.api.ProductsController;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProductsControllerTest {

    @Mock
    private ProductsService productsService;

    @Mock
    private DataBaseUtils dataBaseUtils;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ProductsController productsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Получение списка категорий")
    public void getCategories_ReturnsListOfCategories() throws JsonProcessingException {
        List<Category> categories = Arrays.asList(
                new Category() {{ setCategory_id(1); setName("Category1"); }},
                new Category() {{ setCategory_id(2); setName("Category2"); }}
        );

        when(dataBaseUtils.query(anyString(), eq(Category.class))).thenReturn(categories);
        when(objectMapper.writeValueAsString(categories)).thenReturn("[{\"id\":1,\"name\":\"Category1\"},{\"id\":2,\"name\":\"Category2\"}]");

        ResponseEntity<String> response = productsController.getCategories();

        assertEquals(ResponseEntity.ok("[{\"id\":1,\"name\":\"Category1\"},{\"id\":2,\"name\":\"Category2\"}]"), response);
    }

    @Test
    @DisplayName("Получение отзывов для существующего продукта")
    public void getReviews_ForExistingProduct_ReturnsReviews() throws JsonProcessingException {
        int productId = 1;
        Review review1 = new Review();
        review1.setReview_id(1);
        review1.setDescription("Great product!");
        review1.setRate(5);
        review1.setProduct_id(productId);

        Review review2 = new Review();
        review2.setReview_id(2);
        review2.setDescription("Not bad");
        review2.setRate(3);
        review2.setProduct_id(productId);

        List<Review> reviews = Arrays.asList(review1, review2);

        when(dataBaseUtils.query(anyString(), eq(Review.class))).thenReturn(reviews);
        String expectedJson = "[{\"review_id\":1,\"description\":\"Great product!\",\"rate\":5,\"product_id\":1}," +
                "{\"review_id\":2,\"description\":\"Not bad\",\"rate\":3,\"product_id\":1}]";
        when(objectMapper.writeValueAsString(reviews)).thenReturn(expectedJson);

        ResponseEntity<String> response = productsController.getReviews(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedJson, response.getBody());
    }

    @Test
    @DisplayName("Получение списка продуктов без фильтров")
    public void getProducts_WithoutFilters_ReturnsAllProducts() throws JsonProcessingException {
        List<Product> allProducts = Arrays.asList(
                new Product(1, "Product1", "Category1", 100, 10, false),
                new Product(2, "Product2", "Category2", 150, 15, true)
        );
        when(productsService.findAllMatching(Collections.emptyMap())).thenReturn(allProducts);
        String allProductsJson = "[{\"product_id\":1,\"name\":\"Product1\",\"category\":\"Category1\",\"price\":100,\"quantity\":10,\"discontinued\":false}," +
                "{\"product_id\":2,\"name\":\"Product2\",\"category\":\"Category2\",\"price\":150,\"quantity\":15,\"discontinued\":true}]";
        when(objectMapper.writeValueAsString(allProducts)).thenReturn(allProductsJson);

        ResponseEntity<String> response = productsController.getProducts(Collections.emptyMap());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(allProductsJson, response.getBody());
    }

    @Test
    @DisplayName("Получение списка продуктов с фильтрами")
    public void getProducts_WithFilters_ReturnsFilteredProducts() throws JsonProcessingException {
        Map<String, String> filters = new HashMap<>();
        filters.put("category", "Category1");
        List<Product> filteredProducts = Collections.singletonList(
                new Product(1, "Product1", "Category1", 100, 10, false)
        );
        when(productsService.findAllMatching(filters)).thenReturn(filteredProducts);
        String filteredProductsJson = "[{\"product_id\":1,\"name\":\"Product1\",\"category\":\"Category1\",\"price\":100,\"quantity\":10,\"discontinued\":false}]";
        when(objectMapper.writeValueAsString(filteredProducts)).thenReturn(filteredProductsJson);

        ResponseEntity<String> response = productsController.getProducts(filters);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(filteredProductsJson, response.getBody());
    }


    @Test
    @DisplayName("Получение отзывов для несуществующего продукта")
    public void getReviews_ForNonExistingProduct_ReturnsEmptyList() throws JsonProcessingException {
        int productId = -1;
        List<Review> reviews = Collections.emptyList();
        when(dataBaseUtils.query(anyString(), eq(Review.class))).thenReturn(reviews);
        when(objectMapper.writeValueAsString(reviews)).thenReturn("[]"); // Пустой JSON-массив

        ResponseEntity<String> response = productsController.getReviews(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[]", response.getBody());
    }

    @Test
    @DisplayName("Получение продуктов, когда сервис возвращает null")
    public void getProducts_ServiceReturnsNull_ReturnsEmptyJsonArray() throws JsonProcessingException {
        when(productsService.findAllMatching(any())).thenReturn(null);
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");

        ResponseEntity<String> response = productsController.getProducts(new HashMap<>());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[]", response.getBody());
    }

    @Test
    @DisplayName("Получение отзывов возвращает ошибку сервера, если запрос к БД неудачен")
    public void getReviews_DatabaseQueryFails_ThrowsException() throws JsonProcessingException {
        int productId = 1;
        when(dataBaseUtils.query(anyString(), eq(Review.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productsController.getReviews(productId);
        });

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    @DisplayName("Получение пустого списка категорий")
    public void getCategories_ReturnsEmptyList() throws JsonProcessingException {
        List<Category> categories = Collections.emptyList();
        when(dataBaseUtils.query(anyString(), eq(Category.class))).thenReturn(categories);
        when(objectMapper.writeValueAsString(categories)).thenReturn("[]");

        ResponseEntity<String> response = productsController.getCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[]", response.getBody());
    }

    @Test
    @DisplayName("Отправка отзыва без Principal возвращает BadRequest")
    void postReviewWithoutPrincipalReturnsBadRequest() throws JsonProcessingException {
        Integer productId = 1;
        ProductsController.ReviewLocalDto reviewDto = new ProductsController.ReviewLocalDto(5, "Great product");

        ResponseEntity<String> response = productsController.postReview(productId, reviewDto, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Успешная отправка отзыва возвращает Ok")
    void postReviewWithValidPrincipalReturnsOk() throws JsonProcessingException {
        Integer productId = 1;
        Integer userId = 1;
        ProductsController.ReviewLocalDto reviewDto = new ProductsController.ReviewLocalDto(5, "Great product");
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("username");
        when(userDao.findUserIdByLogin("username")).thenReturn(userId);

        ResponseEntity<String> response = productsController.postReview(productId, reviewDto, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(dataBaseUtils).execute(anyString()); // Проверяем, что был вызван метод execute с любой строкой
    }

    public static class ReviewLocalDtoTest {

        @Test
        @DisplayName("Конструктор корректно инициализирует поля")
        public void constructorCorrectlyInitializesFields() {
            Integer expectedRate = 5;
            String expectedDescription = "Excellent";

            ProductsController.ReviewLocalDto dto = new ProductsController.ReviewLocalDto(expectedRate, expectedDescription);

            assertEquals(expectedRate, dto.getRate());
            assertEquals(expectedDescription, dto.getDescription());
        }

        @Test
        @DisplayName("Установка и получение оценки")
        public void setAndGetRate() {
            ProductsController.ReviewLocalDto dto = new ProductsController.ReviewLocalDto();
            Integer expectedRate = 4;

            dto.setRate(expectedRate);

            assertEquals(expectedRate, dto.getRate());
        }

        @Test
        @DisplayName("Установка и получение описания")
        public void setAndGetDescription() {
            ProductsController.ReviewLocalDto dto = new ProductsController.ReviewLocalDto();
            String expectedDescription = "Good quality";

            dto.setDescription(expectedDescription);

            assertEquals(expectedDescription, dto.getDescription());
        }
    }
}


