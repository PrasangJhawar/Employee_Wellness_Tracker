package org.example.controller;
import java.util.Map;
import java.util.regex.Pattern;
import org.example.dto.LoginResponseDTO;
import org.example.model.Employee;
import org.example.model.Response;
import org.example.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://127.0.0.1:5500") //cors for frontend

public class AuthController {
    private final EmployeeService employeeService;
    public AuthController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerEmployee(@RequestBody Employee employee) {
        //using regular expression for the company email domain
        String emailRegex = "^[a-zA-Z0-9._%+-]+@nucleusteq\\.com$";

        //validating the email
        if (employee.getEmail() == null || !Pattern.matches(emailRegex, employee.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid email. Only company.com emails are allowed."));

        }

        try {
            Employee savedEmployee = employeeService.createEmployee(employee);
            return ResponseEntity.ok(savedEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Employee loginRequest) {
        System.out.println("Received Login Request: " + loginRequest.getEmail() + " - " + loginRequest.getPassword());

        if (employeeService.authenticateEmployee(loginRequest.getEmail(), loginRequest.getPassword())) {
            Optional<Employee> employee = employeeService.getEmployeeByEmail(loginRequest.getEmail());

            if (employee.isPresent()) {
                Employee loggedInEmployee = employee.get();
                return ResponseEntity.ok(new LoginResponseDTO(
                        loggedInEmployee.getId(),
                        loggedInEmployee.getName(),
                        loggedInEmployee.isAdmin()
                ));
            }
        }
        return ResponseEntity.status(401).body("Invalid email or password");
    }



    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable UUID id, @RequestBody Employee updatedEmployee) {
        try {
            Employee savedEmployee = employeeService.updateEmployee(id, updatedEmployee);
            return ResponseEntity.ok(savedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Employee not found");
        }
    }




}
