package integration.utils;

import ru.scheredin.dto.Employee;
import ru.scheredin.services.EmployeesService;

public class EmployeesServiceMock implements EmployeesService {
    @Override
    public Employee getRandomEmployee() {
        Employee randomEmployee = new Employee();
        randomEmployee.setUser_id(0);
        return randomEmployee;
    }
}
