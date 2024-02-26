package ru.scheredin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scheredin.dto.Refund;
import ru.scheredin.services.RefundsService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class RefundsController {
    private final RefundsService refundsService;
    private final ObjectMapper objectMapper;

    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    public static class LocalDto {
        private String description;
    }
    @PostMapping(value = "/{orderId}/refund", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> requestRefund(Principal principal, @PathVariable("orderId") Integer orderId,
                                                 @RequestBody LocalDto description) {
        if (!refundsService.isCouldBeRefunded(orderId)) {
            return ResponseEntity.notFound().build();
        }
        else if (!refundsService.isOwner(principal.getName(), orderId)) {
            return ResponseEntity.status(401).build();
        }else if(!refundsService.createRefund(orderId, description.getDescription())){
            return ResponseEntity.badRequest().build();
        }else{
            return ResponseEntity.ok().build();
        }
    }

    @PutMapping(value = "/refund/{refundId}")
    public ResponseEntity<Integer> approveRefund(@PathVariable("refundId") Integer refundId, Principal principal) {
        if(refundId <= 0){
            return ResponseEntity.badRequest().build();
        }
        else if(refundsService.approveRefund(refundId)){
            return ResponseEntity.ok().build();
        }else{
            return  ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/refund/my", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMyRefunds(Principal principal) throws JsonProcessingException {
        List<Refund> myRefunds = refundsService.getMyRefunds(principal.getName());
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(myRefunds));
    }

    @GetMapping(value = "/refund/assigned", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAssignedRefunds(Principal principal) throws JsonProcessingException {
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(refundsService.getAssignedRefunds(principal.getName())));
    }
}
