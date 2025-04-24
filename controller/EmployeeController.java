package org.example.controller;

import org.example.dto.LoginResponseDTO;
import org.example.model.Employee;
import org.example.model.Response;
import org.example.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    //used in admin-dashboard.js fetchEmployees()
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(){
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    //used in admin-dashboard.js edit-employee()
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Employee>> getEmployeeById(@PathVariable UUID id){
        employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }


    //used in admin-dashboard.js deleteEmployee()
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id){
        employeeService.deleteEmployee(id);
        //status code 204
        return ResponseEntity.noContent().build();
    }


    //used in admin-dashbord.js promoteToAdmin()
    //used in profile.js updateProfile()
    @PutMapping("/{id}/promote")
    public ResponseEntity<Employee> promoteToAdmin(@PathVariable UUID id) {
        Employee updatedEmployee = employeeService.promoteToAdmin(id);
        return ResponseEntity.ok(updatedEmployee);
    }


}
