package ru.scheredin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Employee {
    private Integer user_id;
    private Integer salary;
    private String employment_date;
    private Integer role_id;
}
