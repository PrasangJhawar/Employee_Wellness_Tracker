package org.example.service;

import org.example.model.Employee;
import org.example.model.Response;
import org.example.model.Survey;
import org.example.model.Question;
import org.example.repository.ResponseRepository;
import org.example.repository.SurveyRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final EmployeeRepository employeeRepository;

    public ResponseService(ResponseRepository responseRepository,
                           SurveyRepository surveyRepository,
                           QuestionRepository questionRepository,
                           EmployeeRepository employeeRepository) {
        this.responseRepository = responseRepository;
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.employeeRepository = employeeRepository;
    }

    public Response submitResponse(UUID employeeId, UUID surveyId, UUID questionId, String responseText) {
        // Verify survey, question, and employee existence
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));


        Response response = Response.builder()
                .employee(employee)
                .survey(survey)
                .question(question)
                .responseText(responseText)
                .timestamp(System.currentTimeMillis())
                .build();

        return responseRepository.save(response);
    }

    public List<Response> getResponsesForSurvey(UUID surveyId) {
        return responseRepository.findBySurveyId(surveyId);
    }

    public List<Response> getResponsesForEmployee(UUID employeeId) {
        return responseRepository.findByEmployeeId(employeeId);
    }

    public void deleteResponse(UUID responseId) {
        responseRepository.deleteById(responseId);
    }
}
