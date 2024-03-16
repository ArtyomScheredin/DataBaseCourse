package ru.scheredin.dao;

import ru.scheredin.dto.Product;

import java.util.List;

public interface ProductsDao {
    public List<Product> findAllProducts();
    public List<Integer> findAllProductsNotDiscontinued();
}
