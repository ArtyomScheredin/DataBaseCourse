package ru.scheredin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class Review{
    private int review_id;
    private int rate;
    private String description;
    private int customer_id;
    private int product_id;
}
