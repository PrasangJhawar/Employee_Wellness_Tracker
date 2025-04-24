package org.example.service;

import org.example.model.Employee;
import org.example.model.Response;
import org.example.repository.EmployeeRepository;
import org.example.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;


    //constructor injection
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    //employee creation
    public Employee createEmployee(Employee employee) {
        //checking if employee is present with the help of optional
        Optional<Employee> existingEmployee = employeeRepository.findByEmail(employee.getEmail());
        if (existingEmployee.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        return employeeRepository.save(employee);
    }

    //fetch all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    //fetching employee by id
    public Optional<Employee> getEmployeeById(UUID id) {
        return employeeRepository.findById(id);
    }

    //fetching employee by email
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    //deletion
    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }

    //updating employee, setting values from the updatedEmployee
    public Employee updateEmployee(UUID id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    if (updatedEmployee.getName() != null) {
                        employee.setName(updatedEmployee.getName());
                    }
                    if (updatedEmployee.getEmail() != null) {
                        employee.setEmail(updatedEmployee.getEmail());
                    }
                    if (updatedEmployee.getDepartment() != null) {
                        employee.setDepartment(updatedEmployee.getDepartment());
                    }
                    if (updatedEmployee.getPassword() != null && !updatedEmployee.getPassword().isEmpty()) {
                        employee.setPassword(updatedEmployee.getPassword());
                    }
                    employee.setAdmin(updatedEmployee.isAdmin());

                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }


    //authentication employee during login
    public boolean authenticateEmployee(String email, String password) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        //.get for getting employee object in optional, then .getPassword
        //for accessing the method of the employee
        return employee.isPresent() && employee.get().getPassword().equals(password);
    }
    
    public Employee promoteToAdmin(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        //setting the admin to true
        employee.setAdmin(true);
        return employeeRepository.save(employee);
    }

}
