package ru.scheredin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scheredin.services.OrdersServiceImpl;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersServiceImpl ordersServiceImpl;
    private final ObjectMapper objectMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMyOrders(Principal principal) throws JsonProcessingException {
        if(principal == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(objectMapper.writeValueAsString(ordersServiceImpl.getOrders(principal.getName())));
    }

    /**
     * @param products - product_id, quantity map
     * @return order_id
     */
    @PostMapping()
    public ResponseEntity<Integer> createOrder(@RequestBody Map<Integer, Integer> products, Principal principal) {
        if(principal == null){
            return ResponseEntity.badRequest().build();
        }
        Integer orderId = ordersServiceImpl.createOrder(products, principal.getName());
        if (orderId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.accepted().body(orderId);
    }

}
