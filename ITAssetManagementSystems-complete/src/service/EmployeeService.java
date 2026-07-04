package service;

import dao.EmployeeDAO;
import java.util.ArrayList;
import java.util.List;
import model.Employee;

public class EmployeeService {

    private final List<Employee> employees = new ArrayList<>();
    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = null;
    }

    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        if (employeeDAO != null) {
            employeeDAO.addEmployee(employee);
            return;
        }
        if (findEmployeeById(employee.getEmployeeId()) != null) {
            throw new IllegalArgumentException("Employee ID already exists.");
        }
        employees.add(employee);
    }

    public Employee findEmployeeById(int employeeId) {
        if (employeeDAO != null) {
            return employeeDAO.findEmployeeById(employeeId);
        }
        for (Employee employee : employees) {
            if (employee.getEmployeeId() == employeeId) {
                return employee;
            }
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        if (employeeDAO != null) {
            return employeeDAO.getAllEmployees();
        }
        return new ArrayList<>(employees);
    }
}
