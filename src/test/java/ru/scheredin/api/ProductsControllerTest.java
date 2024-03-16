package ru.scheredin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.scheredin.config.JwtAuthFilter;
import ru.scheredin.dao.UserDaoImpl;
import ru.scheredin.services.ProductsServiceImpl;
import ru.scheredin.utils.DataBaseUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("anna")
@WebMvcTest(ProductsController.class)
@AutoConfigureMockMvc
class ProductsControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private DataBaseUtils dataBaseUtils;
    @MockBean
    private UserDaoImpl userDaoImpl;
    @MockBean
    private ProductsServiceImpl productsServiceImpl;

    private AutoCloseable autoCloseable;

    private  ProductsController underTest;

    //ARGS
    public static final String NAME = "name";
    public static final Integer CATEGORY_ID = 1;
    public static final Integer PRODUCT_ID = 2;
    public static final Integer PRICE = 1000;
    public static final Integer QUANTITY = 1;
    public static final Boolean IS_DISCOUNT = false;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new ProductsController(productsServiceImpl, dataBaseUtils, objectMapper, userDaoImpl);
        this.mockMvc = MockMvcBuilders.standaloneSetup(underTest).build();
        when(dataBaseUtils.execute(any(String.class))).thenReturn(1);
        when(dataBaseUtils.querySingle(any(String.class), any(DataBaseUtils.ResultSetConverter.class))).thenReturn("");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createProductSuccess() throws Exception {
        //give
        String exceptedQuery = String.format("insert into products (name, category_id, price, quantity, discontinued) VALUES ('%s', %d, %d, %d, %b);",
                NAME, CATEGORY_ID, PRICE, QUANTITY, IS_DISCOUNT);
        String URL = "/products";
        String PRODUCT_JSON = new Gson().toJson(new ProductsController.ProductDto(NAME, CATEGORY_ID, PRICE, QUANTITY, IS_DISCOUNT));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URL)
                .content(PRODUCT_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).execute(queryCaptor.capture());
        assertEquals(exceptedQuery, queryCaptor.getValue());
    }

    @Test
    void updateProductPriceNotNull() throws Exception {
        //give
        String exceptedQuery = String.format("update products set price=%d where product_id=%d",
                PRICE, PRODUCT_ID);
        String URL = "/products/{product_id}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(URL, PRODUCT_ID)
                .param("price", String.valueOf(PRICE))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).execute(queryCaptor.capture());
        assertEquals(exceptedQuery, queryCaptor.getValue());
    }

    @Test
    void updateProductQuantityNotNull() throws Exception {
        //give
        String exceptedQuery = String.format("update products set quantity=%d where product_id=%d",
                QUANTITY, PRODUCT_ID);
        String URL = "/products/{product_id}";
        when(dataBaseUtils.execute(any(String.class))).thenReturn(1);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(URL, PRODUCT_ID)
                .param("quantity", String.valueOf(QUANTITY))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).execute(queryCaptor.capture());
        assertEquals(exceptedQuery, queryCaptor.getValue());
    }

    @Test
    void updateProductDiscontinuedNotNull() throws Exception {
        //give
        String exceptedQuery = String.format("update products set discontinued=%b where product_id=%d",
                IS_DISCOUNT, PRODUCT_ID);
        String URL = "/products/{product_id}";
        when(dataBaseUtils.execute(any(String.class))).thenReturn(1);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(URL, PRODUCT_ID)
                .param("discontinued", String.valueOf(IS_DISCOUNT))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        //when
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataBaseUtils, Mockito.times(1)).execute(queryCaptor.capture());
        assertEquals(exceptedQuery, queryCaptor.getValue());
    }

    @Test
    void getNameSuccess() throws Exception {
        //give
        String URL = "/products/{id}";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(URL, PRODUCT_ID);
        //then
        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Конструктор корректно инициализирует поля")
    public void constructorCorrectlyInitializesFields() {
        String expectedName = "name";
        Integer expectedCategoryId = 1;
        Integer expectedPrice = 100;
        Integer expectedQuantity = 1;

        ProductsController.ProductDto dto = new ProductsController.ProductDto(expectedName, expectedCategoryId,
                expectedPrice, expectedQuantity, false);

        assertEquals(expectedName, dto.getName());
        assertEquals(expectedCategoryId, dto.getCategory_id());
        assertEquals(expectedPrice, dto.getPrice());
        assertEquals(expectedQuantity, dto.getQuantity());
    }

    @Test
    @DisplayName("Установка и получение имени")
    public void setAndGetName() {
        ProductsController.ProductDto dto = new ProductsController.ProductDto();
        String expectedName = "name";

        dto.setName(expectedName);

        assertEquals(expectedName, dto.getName());
    }
    @Test
    @DisplayName("Установка и получение цены")
    public void setAndGetPrice() {
        ProductsController.ProductDto dto = new ProductsController.ProductDto();
        Integer expectedPrice = 100;

        dto.setPrice(expectedPrice);

        assertEquals(expectedPrice, dto.getPrice());
    }

    @Test
    @DisplayName("Установка и получение количества")
    public void setAndGetQuantity() {
        ProductsController.ProductDto dto = new ProductsController.ProductDto();
        Integer expectedQuantity = 1;

        dto.setQuantity(expectedQuantity);

        assertEquals(expectedQuantity, dto.getQuantity());
    }

    @Test
    @DisplayName("Установка и получение категории")
    public void setAndGetCategory() {
        ProductsController.ProductDto dto = new ProductsController.ProductDto();
        Integer expectedCategoryId = 1;

        dto.setCategory_id(expectedCategoryId);

        assertEquals(expectedCategoryId, dto.getCategory_id());
    }
}