package ru.scheredin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.scheredin.dao.UserDao;
import ru.scheredin.dto.Category;
import ru.scheredin.dto.Product;
import ru.scheredin.dto.Review;
import ru.scheredin.services.ProductsService;
import ru.scheredin.utils.DataBaseUtils;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;
    private final DataBaseUtils dataBaseUtils;
    private final ObjectMapper objectMapper;
    private final UserDao userDao;
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/categories")
    public ResponseEntity<String> getCategories()
            throws JsonProcessingException {
        List<Category> categories = dataBaseUtils.query("select * from product_categories;", Category.class);
        return ResponseEntity.ok(objectMapper.writeValueAsString(categories));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getProducts(@RequestParam(required = false) Map<String, String> filters)
            throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(productsService.findAllMatching(filters)));
    }

    @GetMapping(value = "/{product_id}/review", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReviews(@PathVariable int product_id) throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(dataBaseUtils.query(String.format("""
                                                                                                           select * from reviews where product_id='%s'""",
                                                                                                   product_id),
                                                                                     Review.class)));
    }



    @NoArgsConstructor
    @Getter
    @Setter
    public static class ReviewLocalDto {
        public ReviewLocalDto(Integer rate, String description) {
            this.rate = rate;
            this.description = description;
        }

        public Integer rate;
        public String description;
    }
    @PostMapping("/{product_id}/review")
    public ResponseEntity<String> postReview(@PathVariable Integer product_id, @RequestBody ReviewLocalDto reviewLocalDto,
                                             Principal principal) throws JsonProcessingException {
        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }
        Integer userId = userDao.findUserIdByLogin(principal.getName());
        dataBaseUtils.execute(String.format("""
                                                                                  insert into reviews (rate, description, customer_id, product_id) values (
                                                                                  %d,'%s',%d,%d
                                                                                  );""", reviewLocalDto.rate, reviewLocalDto.description, userId,
                                                                          product_id));
        return ResponseEntity.ok().build();
    }
    @NoArgsConstructor
    @Getter
    @Setter
    private static class ProductDto {
        private String name;
        private int category_id;
        private int price;
        private int quantity;
        private boolean discontinued;
    }
    @GetMapping("/{id}")
    public ResponseEntity<String> getName(@PathVariable Integer id
    ) {
        try {
            String name = dataBaseUtils.querySingle(String.format("""
                                                                              select name from products where product_id=%d""",
                                                                      id),
                                                        t -> t.getString(1));
            return ResponseEntity.ok(name);
        } catch (Throwable e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createProduct(@RequestBody ProductDto product
    ) {
        try {
            dataBaseUtils.execute(String.format("""
                                                        insert into products (name, category_id, price, quantity, discontinued) VALUES ('%s', %d, %d, %d, %b);""",
                                                product.getName(), product.getCategory_id(), product.getPrice(), product.getQuantity(), product.isDiscontinued()));
            return ResponseEntity.ok().build();
        } catch (Throwable e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{product_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateProduct(@PathVariable Integer product_id,
                                                @RequestParam(required = false) Integer price,
                                                @RequestParam(required = false) Integer quantity,
                                                @RequestParam(required = false) Boolean discontinued
    ) {
        try {
            if (price != null) {
                dataBaseUtils.execute(String.format("""
                                                            update products set price=%d where product_id=%d""",
                                                    price, product_id));
            }
            if (quantity != null) {
                dataBaseUtils.execute(String.format("""
                                                            update products set quantity=%d where product_id=%d""",
                                                    quantity, product_id));
            }
            if (discontinued != null) {
                dataBaseUtils.execute(String.format("""
                                                            update products set discontinued=%b where product_id=%d""",
                                                    discontinued, product_id));
            }
            return ResponseEntity.ok().build();
        } catch (Throwable e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
