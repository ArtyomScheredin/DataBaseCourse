package ru.scheredin.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.scheredin.dto.Employee;
import ru.scheredin.utils.DataBaseUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmployeesDao {
    private final DataBaseUtils dataBaseUtils;

    public List<Employee> findAll() {
        return dataBaseUtils.query("select * from employees", Employee.class);
    }
}
