package ru.scheredin.dao;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.scheredin.dto.Product;
import ru.scheredin.utils.DataBaseUtils;

import java.util.List;

@Repository
@AllArgsConstructor
public class ProductsDao {
    public DataBaseUtils dataBaseUtils;

    public List<Product> findAllProducts() {
        return dataBaseUtils.query("SELECT * FROM products;", Product.class);
    }
}
