package ru.scheredin.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.scheredin.dto.AuthenticationRequest;
import ru.scheredin.dto.Category;
import ru.scheredin.dto.Employee;
import ru.scheredin.dto.Order;
import ru.scheredin.dto.Product;
import ru.scheredin.dto.Refund;
import ru.scheredin.dto.Review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DTOTests {

    @Test
    @DisplayName("Тест конструктора и геттеров для AuthenticationRequest")
    void authenticationRequestConstructorAndGettersTest() {
        AuthenticationRequest request = new AuthenticationRequest("testName", "testPassword");
        assertEquals("testName", request.getName());
        assertEquals("testPassword", request.getPassword());
    }

    @Test
    @DisplayName("Тест конструктора и геттеров для Category")
    void categoryConstructorAndGettersTest() {
        Category category = new Category();
        category.setCategory_id(1);
        category.setName("Тестовая категория");
        assertEquals(1, category.getCategory_id());
        assertEquals("Тестовая категория", category.getName());
    }

    @Test
    @DisplayName("Тест конструктора и геттеров для Employee")
    void employeeConstructorAndGettersTest() {
        Employee employee = new Employee();
        employee.setUser_id(1);
        employee.setSalary(50000);
        employee.setEmployment_date("2024-02-20");
        employee.setRole_id(2);

        assertEquals(1, employee.getUser_id());
        assertEquals(50000, employee.getSalary());
        assertEquals("2024-02-20", employee.getEmployment_date());
        assertEquals(2, employee.getRole_id());
    }

    @Test
    @DisplayName("Тест конструктора и геттеров для Order")
    void orderConstructorAndGettersTest() {
        Order order = new Order();
        order.setOrder_id(1);
        order.setCustomer_id("Customer123");
        order.setOrder_date("2024-02-20");
        order.setRecieve_date("2024-02-25");

        assertEquals(1, order.getOrder_id());
        assertEquals("Customer123", order.getCustomer_id());
        assertEquals("2024-02-20", order.getOrder_date());
        assertEquals("2024-02-25", order.getRecieve_date());
    }

    @Test
    @DisplayName("Тест конструктора и геттеров для Product")
    void productConstructorAndGettersTest() {
        Product product = new Product();
        product.setProduct_id(1);
        product.setName("Тестовый продукт");
        product.setCategory("Тестовая категория");
        product.setPrice(10);
        product.setQuantity(100);
        product.setDiscontinued(false);

        assertEquals(1, product.getProduct_id());
        assertEquals("Тестовый продукт", product.getName());
        assertEquals("Тестовая категория", product.getCategory());
        assertEquals(10, product.getPrice());
        assertEquals(100, product.getQuantity());
        assertFalse(product.isDiscontinued());

        product.setDiscontinued(true);
        assertTrue(product.isDiscontinued());
    }

    @Test
    @DisplayName("Тест конструктора и геттеров для Refund")
    void refundConstructorAndGettersTest() {
        Refund refund = new Refund();
        refund.setRefund_id(1);
        refund.setOrder_id(100);
        refund.setDescription("Тестовый возврат");
        refund.setApproved(true);
        refund.setEmployee_id(50);

        assertEquals(1, refund.getRefund_id());
        assertEquals(100, refund.getOrder_id());
        assertEquals("Тестовый возврат", refund.getDescription());
        assertTrue(refund.isApproved());
        assertEquals(50, refund.getEmployee_id());

        refund.setApproved(false);
        assertFalse(refund.isApproved());
    }

    @Test
    @DisplayName("Тест конструктора и геттеров для Review")
    void reviewConstructorAndGettersTest() {
        Review review = new Review();
        review.setReview_id(1);
        review.setRate(4);
        review.setDescription("Тестовый отзыв");
        review.setCustomer_id(100);
        review.setProduct_id(50);

        assertEquals(1, review.getReview_id());
        assertEquals(4, review.getRate());
        assertEquals("Тестовый отзыв", review.getDescription());
        assertEquals(100, review.getCustomer_id());
        assertEquals(50, review.getProduct_id());

        review.setRate(5);
        assertEquals(5, review.getRate());
    }
}
