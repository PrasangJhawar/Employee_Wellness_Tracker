package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.EmployeeHealthDTO;
import org.example.model.Employee;
import org.example.model.EmployeeHealth;
import org.example.model.Survey;
import org.example.repository.EmployeeHealthRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.SurveyRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeHealthService {

    private final EmployeeRepository employeeRepository;
    private final SurveyRepository surveyRepository;
    private final EmployeeHealthRepository employeeHealthRepository;

    public void evaluateAndSaveHealth(EmployeeHealthDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Survey survey = surveyRepository.findById(dto.getSurveyId())
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        int totalScore = dto.getResponses().stream().mapToInt(Integer::intValue).sum();
        int numberOfQuestions = dto.getResponses().size();
        int maxScore = numberOfQuestions * 5;
        int rangeSize = maxScore / 5;

        String result;

        if (totalScore <= rangeSize) {
            result = "Very Good";
        } else if (totalScore <= 2 * rangeSize) {
            result = "Good";
        } else if (totalScore <= 3 * rangeSize) {
            result = "Average";
        } else if (totalScore <= 4 * rangeSize) {
            result = "Bad";
        } else {
            result = "Very Bad";
        }

        EmployeeHealth health = EmployeeHealth.builder()
                .employee(employee)
                .survey(survey)
                .surveyType(dto.getSurveyType())
                .surveyResult(result)
                .build();

        employeeHealthRepository.save(health);
    }
}
