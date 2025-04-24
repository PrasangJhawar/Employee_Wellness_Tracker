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
    private final EmployeeService employeeService; //injecting to check admin status

    public SurveyService(SurveyRepository surveyRepository, EmployeeService employeeService) {
        this.surveyRepository = surveyRepository;
        this.employeeService = employeeService;
    }

    //authenticating if the employee is an admin
    public boolean isAdmin(UUID employeeId) {
        Optional<Employee> employee = employeeService.getEmployeeById(employeeId);
        return employee.map(Employee::isAdmin).orElse(false); //returns true if admin, else false
    }

    // create a new survey
    public Survey createSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    // fetch all surveys
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    //fetching survey by id
    public Optional<Survey> getSurveyById(UUID id) {
        return surveyRepository.findById(id);
    }
    
    // Delete a survey
    public void deleteSurvey(UUID id) {
        surveyRepository.deleteById(id);
    }
}