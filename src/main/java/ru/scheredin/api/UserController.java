package ru.scheredin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.scheredin.services.CustomerService;
import ru.scheredin.utils.DataBaseUtils;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final CustomerService customerService;
    private final UserDetailsService userDetailsService;
    private final DataBaseUtils dataBaseUtils;
    private final ObjectMapper objectMapper;

    @GetMapping("/whoami")
    public ResponseEntity<String> blockUser(Principal principal) throws JsonProcessingException {
        if (principal == null) {
            return ResponseEntity.notFound().build();
        }
        UserDetails user = userDetailsService.loadUserByUsername(principal.getName());
        return ResponseEntity.ok(
                objectMapper.writeValueAsString(Map.of("name", user.getUsername(), "role",
                                                       user.getAuthorities().iterator().next().toString()
                                                               .substring(5))));
    }

    @PutMapping("/{user_id}/ban")
    public ResponseEntity<String> blockUser(@PathVariable Integer user_id, @RequestParam Boolean banned) {
        dataBaseUtils.execute(String.format("""
                                                    update users set blocked=%b where user_id=%d;""", banned, user_id));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{user_id}/salary")
    public ResponseEntity<String> changeSalary(@PathVariable Integer user_id, @RequestParam Integer newSalary) {
        dataBaseUtils.execute(String.format("""
                                                    update employees set salary=%d where user_id=%d;""", newSalary,
                                            user_id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalance(Principal principal) {
        return ResponseEntity.ok(customerService.getBalance(principal.getName()));
    }

    @PutMapping("/balance")
    public ResponseEntity<String> updateBalance(@RequestParam Integer newBalance, Principal principal) {
        if (customerService.updateBalance(principal.getName(), newBalance)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PersonDto {
        private int user_id;
        private String login;
        private String name;
        private boolean blocked;
        private int salary;
    }

    @GetMapping("/people")
    public ResponseEntity<String> getPeople() throws JsonProcessingException {
        return ResponseEntity.ok(
                objectMapper.writeValueAsString(dataBaseUtils.query(
                        "select u.user_id, u.login, u.name, u.password, u.blocked, e.salary from users u left join employees e on u.user_id = e.user_id",
                        PersonDto.class)));

    }

    @PostMapping("/customer/")
    public ResponseEntity<String> saveCustomer(@RequestBody PersonDto personDto) throws JsonProcessingException {
        return ResponseEntity.ok(
                objectMapper.writeValueAsString(dataBaseUtils.query(
                        String.format("save_customer('%s','%s', '%s');", personDto.login, personDto.name, personDto.blocked),
                        PersonDto.class)));

    }
}
