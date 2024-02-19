package ru.scheredin.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.ProductsDao;
import ru.scheredin.dto.Product;
import ru.scheredin.utils.DataBaseUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ProductsService {
    private ProductsDao productsDao;
    private final DataBaseUtils dataBaseUtils;

    public List<Product> findAllMatching(Map<String, String> filters) {
        Stream<Product> result = productsDao.findAllProducts().stream().filter(e -> (
                        (!filters.containsKey("category") || (e.getCategory().equals(filters.get("category")))))
                       && (!filters.containsKey("price_min") || ((e.getPrice() > Integer.parseInt(filters.get("price_min")))))
                       && (!filters.containsKey("price_max") || ((e.getPrice() < Integer.parseInt(filters.get("price_max")))))
                       && (!filters.containsKey("price") || ((e.getPrice() == Integer.parseInt(filters.get("price")))))
                       && (!filters.containsKey("discontinued") || ((e.isDiscontinued() == Boolean.parseBoolean(filters.get("discontinued")))))
                       && (!filters.containsKey("quantity_min") || ((e.getQuantity() > Integer.parseInt(filters.get("quantity_min")))))
                       && (!filters.containsKey("name") || ((e.getName().contains(filters.get("name"))))
                ));
        if ((filters.containsKey("sort"))) {
            if (filters.get("sort").equals("ascending")) {
                result = result.sorted(Comparator.comparingInt(Product::getPrice));
            } else {
                result = result.sorted((e1, e2) -> ~Integer.compare(e1.getPrice(), e2.getPrice()) + 1);
            }
        }
        List<Product> products = result.toList();
        return products;
    }

    public List<Integer> findAllProductsNotDiscontinued() {
        return productsDao.findAllProductsNotDiscontinued();
    }
}
