package org.example.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class EmployeeHealthDTO {
    private UUID employeeId;
    private UUID surveyId;
    private String surveyType;
    private List<Integer> responses;
}
