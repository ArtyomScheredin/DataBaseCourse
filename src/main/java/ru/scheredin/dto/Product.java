package ru.scheredin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    private int product_id;
    private String name;
    private int category_id;
    private int price;
    private int quantity;
    private boolean discontinued;
}
