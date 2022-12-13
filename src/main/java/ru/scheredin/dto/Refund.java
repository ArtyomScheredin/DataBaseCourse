package ru.scheredin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Getter
@Setter
public class Refund {
    private Integer refund_id;
    private Integer order_id;
    private String description;
    private Boolean approved;
    private Integer employee_id;
}
