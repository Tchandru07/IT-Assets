package dao;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Employee;

public class EmployeeDAO {

    public void addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (employee_id, employee_name, email, phone, department_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employee.getEmployeeId());
            statement.setString(2, employee.getEmployeeName());
            statement.setString(3, employee.getEmail());
            statement.setString(4, employee.getPhone());
            statement.setInt(5, employee.getDepartmentId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to add employee.", exception);
        }
    }

    public Employee findEmployeeById(int employeeId) {
        String sql = "SELECT employee_id, employee_name, email, phone, department_id "
                + "FROM employees WHERE employee_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEmployee(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to find employee.", exception);
        }

        return null;
    }

    public List<Employee> getAllEmployees() {
        String sql = "SELECT employee_id, employee_name, email, phone, department_id "
                + "FROM employees ORDER BY employee_id";
        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                employees.add(mapEmployee(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to list employees.", exception);
        }

        return employees;
    }

    private Employee mapEmployee(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt("employee_id"),
                resultSet.getString("employee_name"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getInt("department_id")
        );
    }
}
