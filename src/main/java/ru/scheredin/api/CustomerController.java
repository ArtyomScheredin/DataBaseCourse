package ru.scheredin.api;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.scheredin.services.CustomerService;

import java.security.Principal;

@RestController
@RequestMapping("/customer")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @GetMapping
    public ResponseEntity<String> sayYourName() {
        return ResponseEntity.ok("Customer");
    }

    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalance(Principal principal) {
        return ResponseEntity.ok(customerService.getBalance(principal.getName()));
    }
    @PutMapping("/balance")
    public ResponseEntity<String> updateBalance(@RequestParam Integer newBalance, Principal principal) {
        if(customerService.updateBalance(principal.getName(), newBalance)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
