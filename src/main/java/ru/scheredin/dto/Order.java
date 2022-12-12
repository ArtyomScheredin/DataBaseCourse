package ru.scheredin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Order {
    private int order_id;
    private String customer_id;
    private String order_date;
    private String recieve_date;
}
