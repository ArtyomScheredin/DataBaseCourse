package ru.scheredin.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:test.properties")
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void test() {

    }
    @AfterEach
    void tearDown() {
    }
}