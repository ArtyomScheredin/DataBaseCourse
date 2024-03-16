package integration.utils;

import ru.scheredin.dao.ProductsDao;
import ru.scheredin.dto.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsDaoImplMock implements ProductsDao {
    @Override
    public List<Product> findAllProducts() {

        List<Product> productList = new ArrayList<>();

        productList.add(new Product(0, "produc_1", "category_1",
                5000, 20, false));

        productList.add(new Product(1, "produc_2", "category_1",
                2000, 16, true));

        productList.add(new Product(2, "produc_3", "category_2",
                1500, 40, false));

        productList.add(new Product(3, "produc_4", "category_3",
                5500, 2, false));

        productList.add(new Product(4, "produc_5", "category_4",
                10000, 1, true));

        return productList;
    }

    @Override
    public List<Integer> findAllProductsNotDiscontinued() {
        List<Integer> productList = new ArrayList<>();

        productList.add(0);
        productList.add(2);
        productList.add(3);

        return productList;
    }
}
