package ru.scheredin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Refund {
    private Integer refund_id;
    private Integer order_id;
    private String description;
    private boolean approved;
    private Integer employee_id;
}
