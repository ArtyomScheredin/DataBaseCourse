package ru.scheredin.dao;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.scheredin.dto.Product;
import ru.scheredin.utils.DataBaseUtils;

import java.util.List;

@Repository
@AllArgsConstructor
public class ProductsDao {
    public DataBaseUtils dataBaseUtils;

    public List<Product> findAllProducts() {
        return dataBaseUtils.query("select *, pc.name as category from products join product_categories pc using(category_id);", Product.class);
    }

    public List<Integer> findAllProductsNotDiscontinued() {
        return dataBaseUtils.query("SELECT * from get_available_products_ids();",
                                   r -> r.getInt(1));
    }
}
