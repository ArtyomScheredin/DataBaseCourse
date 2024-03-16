package ru.scheredin.services;

import ru.scheredin.dto.Product;

import java.util.List;
import java.util.Map;

public interface ProductsService {
    public List<Product> findAllMatching(Map<String, String> filters);
    public List<Integer> findAllProductsNotDiscontinued();
}
