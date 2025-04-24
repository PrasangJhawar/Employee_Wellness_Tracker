package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.EmployeeHealthDTO;
import org.example.service.EmployeeHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee-health")
@RequiredArgsConstructor
public class EmployeeHealthController {

    private final EmployeeHealthService employeeHealthService;

    //used in survey-details.js submitResponses()
    @PostMapping("/evaluate")
    public ResponseEntity<String> evaluateAndSaveHealth(@RequestBody EmployeeHealthDTO dto) {
        employeeHealthService.evaluateAndSaveHealth(dto);
        return ResponseEntity.ok("Employee health evaluated and saved successfully.");
    }
}
