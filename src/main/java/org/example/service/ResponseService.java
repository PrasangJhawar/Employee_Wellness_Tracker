package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.ResponseDTO;
import org.example.model.Employee;
import org.example.model.Question;
import org.example.model.Response;
import org.example.model.Survey;
import org.example.repository.EmployeeRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.ResponseRepository;
import org.example.repository.SurveyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public ResponseDTO submitResponse(UUID employeeId, UUID surveyId, UUID questionId, String responseText) {
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

        Response savedResponse = responseRepository.save(response);

        return new ResponseDTO(
                savedResponse.getId(),
                savedResponse.getResponseText(),
                savedResponse.getEmployee().getId(),
                savedResponse.getQuestion().getId(),
                savedResponse.getQuestion().getText(), // Fetching question text
                savedResponse.getSurvey().getId(),
                response.getSurvey().getTitle(),
                response.getSurvey().getDescription()
        );
    }

    public List<ResponseDTO> getResponsesForSurvey(UUID surveyId) {
        return responseRepository.findBySurveyId(surveyId).stream()
                .map(response -> new ResponseDTO(
                        response.getId(),
                        response.getResponseText(),
                        response.getEmployee().getId(),
                        response.getQuestion().getId(),
                        response.getQuestion().getText(),
                        response.getSurvey().getId(),
                        response.getSurvey().getTitle(),
                        response.getSurvey().getDescription()
                ))
                .collect(Collectors.toList());
    }

    public List<ResponseDTO> getResponsesForEmployee(UUID employeeId) {
        return responseRepository.findByEmployeeId(employeeId).stream()
                .map(response -> new ResponseDTO(
                        response.getId(),
                        response.getResponseText(),
                        response.getEmployee().getId(),
                        response.getQuestion().getId(),
                        response.getQuestion().getText(),
                        response.getSurvey().getId(),
                        response.getSurvey().getTitle(),
                        response.getSurvey().getDescription()
                ))
                .collect(Collectors.toList());
    }

    public List<ResponseDTO> getResponsesForSurveyByEmployee(UUID surveyId, UUID employeeId) {
        return responseRepository.findBySurveyIdAndEmployeeId(surveyId, employeeId).stream()
                .map(response -> new ResponseDTO(
                        response.getId(),
                        response.getResponseText(),
                        response.getEmployee().getId(),
                        response.getQuestion().getId(),
                        response.getQuestion().getText(),  // fetching the actual question text
                        response.getSurvey().getId(),
                        response.getSurvey().getTitle(),  // fetching  survey title
                        response.getSurvey().getDescription() // fetching the survey description
                ))
                .collect(Collectors.toList());
    }


    public void deleteResponse(UUID responseId) {
        responseRepository.deleteById(responseId);
    }

    @Transactional
    public void deleteResponsesByEmployeeAndSurvey(UUID employeeId, UUID surveyId) {
        responseRepository.deleteByEmployeeIdAndSurveyId(employeeId, surveyId);
    }

}
