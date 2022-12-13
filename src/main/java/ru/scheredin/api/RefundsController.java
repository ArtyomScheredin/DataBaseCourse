package ru.scheredin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.scheredin.services.RefundsService;

import java.security.Principal;

@RestController
@RequestMapping("/order/refund")
@RequiredArgsConstructor
public class RefundsController {
    private final RefundsService refundsService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/my")
    public ResponseEntity<Integer> requestRefund(Principal principal, @RequestParam Integer orderId,
                                                 @RequestParam String description) {
        if (refundsService.isCouldBeRefunded(orderId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (refundsService.isOwner(principal.getName(), orderId)) {
            return ResponseEntity.status(HttpStatus.resolve(401)).build();
        }
        refundsService.createRefund(orderId, description);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/assigned")
    public ResponseEntity<Integer> approveRefund(Principal principal, Integer refundId) {
        if (refundsService.isAssignedEmployee(principal.getName(), refundId)) {
            return ResponseEntity.status(HttpStatus.resolve(401)).build();
        }
        refundsService.approveRefund(refundId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMyRefunds(Principal principal) throws JsonProcessingException {
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(refundsService.getMyRefunds(principal.getName())));
    }

    @GetMapping(value = "/assigned", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAssignedRefunds(Principal principal) throws JsonProcessingException {
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(refundsService.getAssignedRefunds(principal.getName())));
    }
}
