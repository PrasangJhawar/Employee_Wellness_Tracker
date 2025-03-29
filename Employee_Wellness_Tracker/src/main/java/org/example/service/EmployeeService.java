package org.example.service;

import org.example.model.Employee;
import org.example.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee) {
        Optional<Employee> existingEmployee = employeeRepository.findByEmail(employee.getEmail());
        if (existingEmployee.isPresent()) {
            throw new IllegalArgumentException("Email already exists!");
        }
        return employeeRepository.save(employee);
    }


    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(UUID id){
        return employeeRepository.findById(id);
    }

    public Optional<Employee> getEmployeeByEmail(String email){
        return employeeRepository.findByEmail(email);
    }

    public void deleteEmployee(UUID id){
        employeeRepository.deleteById(id);
    }

    public Employee updateEmployee(UUID id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(updatedEmployee.getName());
                    employee.setEmail(updatedEmployee.getEmail());
                    employee.setDepartment(updatedEmployee.getDepartment());
                    employee.setAdmin(updatedEmployee.isAdmin());
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }



}
