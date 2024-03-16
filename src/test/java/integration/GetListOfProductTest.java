package integration;

import integration.utils.ProductsDaoImplMock;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import ru.scheredin.dao.ProductsDao;
import ru.scheredin.dto.Product;
import ru.scheredin.services.ProductsServiceImpl;
import ru.scheredin.utils.DataBaseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tags({
        @Tag("integration"),
        @Tag("denis")
})
public class GetListOfProductTest {
    private final ProductsDao productsDao = new ProductsDaoImplMock();
    @Mock
    private DataBaseUtils dataBaseUtils;

    private ProductsServiceImpl productsService = new ProductsServiceImpl(productsDao, dataBaseUtils);
    @Test
    @DisplayName("Получение двух списка продуктов:\n 1) С фильтрами\n 2) Без скидки\n")
    void GetListOfProductTest(){
        List<Integer> listOfProProductsNotDiscontinued = productsService.findAllProductsNotDiscontinued();

        Map<String, String> filters = new HashMap<>();
        filters.put("price_min", "2000");
        filters.put("price_max", "10001");

        List<Product> productList = productsService.findAllMatching(filters);

        Map<String, String> filters_2 = new HashMap<>();
        filters_2.put("category", "category_1");

        List<Product> productList_2 = productsService.findAllMatching(filters_2);

        Map<String, String> filters_3 = new HashMap<>();
        filters_3.put("sort", "ascending");
        filters_3.put("quantity_min", "2");

        List<Product> productList_3 = productsService.findAllMatching(filters_3);

        Assertions.assertEquals(3, listOfProProductsNotDiscontinued.size(), "Не полный список продуктов без скидок");

        Assertions.assertEquals(3, productList.size(), "Не полный список продуктов по ценовым фильтрам");

        Assertions.assertEquals(2, productList_2.size(), "Не полный список продуктов по категориям");

        Assertions.assertEquals(1500, productList_3.get(0).getPrice(), "Проверка сортировки по возрастанию");
        Assertions.assertEquals(5000, productList_3.get(productList_3.size()-1).getPrice(),
                "Проверка сортировки по возрастанию");
        Assertions.assertEquals(3, productList_3.size(), "Проверка сортировки по кол-ву товара");
    }
}
