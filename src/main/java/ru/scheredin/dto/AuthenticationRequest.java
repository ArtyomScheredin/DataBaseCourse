package ru.scheredin.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class AuthenticationRequest {
    private String name;
    private String password;
}
