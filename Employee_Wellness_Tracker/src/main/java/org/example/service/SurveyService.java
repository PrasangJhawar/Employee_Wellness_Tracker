package org.example.service;

import org.example.model.Survey;
import org.example.repository.SurveyRepository;
import org.example.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final EmployeeService employeeService; // Inject EmployeeService to check admin status

    public SurveyService(SurveyRepository surveyRepository, EmployeeService employeeService) {
        this.surveyRepository = surveyRepository;
        this.employeeService = employeeService;
    }

    // Method to check if the employee is an admin
    public boolean isAdmin(UUID employeeId) {
        Optional<Employee> employee = employeeService.getEmployeeById(employeeId);
        return employee.map(Employee::isAdmin).orElse(false); // Returns true if admin, else false
    }

    // Create a new survey
    public Survey createSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    // Get all surveys
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    // Get survey by ID
    public Optional<Survey> getSurveyById(UUID id) {
        return surveyRepository.findById(id);
    }

    // Update an existing survey (only if the user is an admin)
    public Survey updateSurvey(UUID id, Survey updatedSurvey, UUID employeeId) {
        // Check if the employee is an admin
        if (!isAdmin(employeeId)) {
            throw new RuntimeException("Only admins can update surveys");
        }

        // If the employee is an admin, update the survey
        return surveyRepository.findById(id)
                .map(existingSurvey -> {
                    existingSurvey.setTitle(updatedSurvey.getTitle());
                    existingSurvey.setDescription(updatedSurvey.getDescription());
                    return surveyRepository.save(existingSurvey);
                })
                .orElseThrow(() -> new RuntimeException("Survey not found"));
    }

    // Delete a survey
    public void deleteSurvey(UUID id) {
        surveyRepository.deleteById(id);
    }
}
