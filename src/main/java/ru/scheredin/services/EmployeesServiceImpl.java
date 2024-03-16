package ru.scheredin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.scheredin.dao.EmployeesDao;
import ru.scheredin.dto.Employee;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeesServiceImpl implements EmployeesService{
    private final EmployeesDao employeesDao;

    public Employee getRandomEmployee() {
        List<Employee> employees = employeesDao.findAll();
        return employees.get((int) (Math.random() * Integer.MAX_VALUE) % employees.size());
    }
}
