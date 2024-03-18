package ru.scheredin.config.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.scheredin.dao.EmployeesDao;
import ru.scheredin.dto.Employee;
import ru.scheredin.services.EmployeesService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Tag("lera")
public class EmployeesServiceTest {

    @Mock
    private EmployeesDao employeesDao;

    @InjectMocks
    private EmployeesService employeesService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Get a random employee from a non-empty list")
    public void whenEmployeeListIsNotEmpty_thenShouldReturnRandomEmployee() {
        Employee employee1 = new Employee();
        employee1.setUser_id(1);
        employee1.setSalary(50000);
        employee1.setEmployment_date("2020-01-01");
        employee1.setRole_id(2);

        Employee employee2 = new Employee();
        employee2.setUser_id(2);
        employee2.setSalary(60000);
        employee2.setEmployment_date("2020-02-01");
        employee2.setRole_id(3);

        List<Employee> givenEmployees = Arrays.asList(employee1, employee2);
        when(employeesDao.findAll()).thenReturn(givenEmployees);

        Employee randomEmployee = employeesService.getRandomEmployee();

        assertNotNull(randomEmployee);
        assertTrue(givenEmployees.contains(randomEmployee));
    }

    @Test
    @DisplayName("Get a random employee from a list with single employee")
    public void whenEmployeeListHasOneEmployee_thenShouldReturnThatEmployee() {
        Employee singleEmployee = new Employee();
        singleEmployee.setUser_id(1);
        singleEmployee.setSalary(70000);
        singleEmployee.setEmployment_date("2020-03-01");
        singleEmployee.setRole_id(4);

        when(employeesDao.findAll()).thenReturn(Collections.singletonList(singleEmployee));

        Employee randomEmployee = employeesService.getRandomEmployee();

        assertNotNull(randomEmployee);
        assertEquals(singleEmployee, randomEmployee);
    }
    @Test
    @DisplayName("Test getRandomEmployee method - лера")
    public void testGetRandomEmployee() {
        // Arrange
        Employee employee1 = new Employee();
        employee1.setUser_id(1);
        employee1.setSalary(50000);
        employee1.setEmployment_date("2022-01-01");
        employee1.setRole_id(1);

        Employee employee2 = new Employee();
        employee2.setUser_id(2);
        employee2.setSalary(60000);
        employee2.setEmployment_date("2022-02-01");
        employee2.setRole_id(2);

        List<Employee> mockEmployees = Arrays.asList(employee1, employee2);

        when(employeesDao.findAll()).thenReturn(mockEmployees);

        // Act
        Employee randomEmployee = employeesService.getRandomEmployee();

        // Assert
        assertNotNull(randomEmployee);
        assertTrue(mockEmployees.contains(randomEmployee));
    }

}


