package ru.scheredin.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager")
public class ManagerController {
    @GetMapping
    public ResponseEntity<String> sayYourName() {

        return ResponseEntity.ok(
                "Manager");
    }
}
