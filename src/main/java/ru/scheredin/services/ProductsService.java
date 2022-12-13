package ru.scheredin.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.ProductsDao;
import ru.scheredin.dto.Product;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ProductsService {
    private ProductsDao productsDao;

    public List<Product> findAllMatching(Map<String, String> filters) {
        Stream<Product> result = productsDao.findAllProducts().stream().filter(e -> (
                        (!filters.containsKey("category") || (e.getCategory_id() == Integer.parseInt(filters.get("category")))))
                       && (!filters.containsKey("price_min") || ((e.getPrice() > Integer.parseInt(filters.get("price_min")))))
                       && (!filters.containsKey("price_max") || ((e.getPrice() < Integer.parseInt(filters.get("price_max")))))
                       && (!filters.containsKey("price") || ((e.getPrice() == Integer.parseInt(filters.get("price")))))
                       && (!filters.containsKey("discontinued") || ((e.isDiscontinued() == Boolean.parseBoolean(filters.get("discontinued")))))
                       && (!filters.containsKey("quantity_min") || ((e.getQuantity() > Integer.parseInt(filters.get("quantity_min")))))
                       && (!filters.containsKey("name") || ((e.getName().equals(filters.get("name"))))
                ));
        if ((filters.containsKey("sort_price_asc"))) {
            result = result.sorted((e1, e2) -> Integer.compare(e1.getPrice(), e2.getProduct_id()));
        }
        if (filters.containsKey("sort_price_desc")) {
            result = result.sorted((e1, e2) -> ~Integer.compare(e1.getPrice(), e2.getProduct_id()) + 1);
        }
        return result.toList();
    }

    public List<Integer> findAllProductsNotDiscontinued() {
        return productsDao.findAllProductsNotDiscontinued();
    }
}
