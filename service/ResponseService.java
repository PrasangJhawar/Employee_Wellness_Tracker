package org.example.service;
import java.util.*;
import jakarta.transaction.Transactional;
import org.example.dto.EmployeeHealthDTO;
import org.example.dto.ResponseDTO;
import org.example.model.Employee;
import org.example.model.Question;
import org.example.model.Response;
import org.example.model.Survey;
import org.example.repository.EmployeeRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.ResponseRepository;
import org.example.repository.SurveyRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeHealthService employeeHealthService;

    public ResponseService(ResponseRepository responseRepository,
                           SurveyRepository surveyRepository,
                           QuestionRepository questionRepository,
                           EmployeeRepository employeeRepository,
                           EmployeeHealthService employeeHealthService) {
        this.responseRepository = responseRepository;
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.employeeRepository = employeeRepository;
        this.employeeHealthService = employeeHealthService;
    }

    //edit- save in db in once(take qs and ans in one dto)
    //edit remove employee id check
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
                .build();

        Response savedResponse = responseRepository.save(response);

        return new ResponseDTO(
                savedResponse.getId(),
                savedResponse.getResponseText(),
                savedResponse.getEmployee().getId(),
                savedResponse.getQuestion().getId(),
                savedResponse.getQuestion().getText(),
                savedResponse.getSurvey().getId(),
                savedResponse.getSurvey().getTitle(),
                savedResponse.getSurvey().getDescription()
        );
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



    public ResponseDTO editResponse(UUID responseId, String newResponseText) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        response.setResponseText(newResponseText); //updating the answer
        Response updatedResponse = responseRepository.save(response); //saving to DB

        return new ResponseDTO(
                updatedResponse.getId(),
                updatedResponse.getResponseText(),
                updatedResponse.getEmployee().getId(),
                updatedResponse.getQuestion().getId(),
                updatedResponse.getQuestion().getText(),
                updatedResponse.getSurvey().getId(),
                updatedResponse.getSurvey().getTitle(),
                updatedResponse.getSurvey().getDescription()
        );
    }


    //filtered by survey and responses together
    public List<ResponseDTO> getResponsesForSurveyByEmployee(UUID surveyId, UUID employeeId) {
        return responseRepository.findBySurveyIdAndEmployeeId(surveyId, employeeId).stream()
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

    public void deleteResponse(UUID responseId) {
        responseRepository.deleteById(responseId);
    }

    @Transactional //should occur in one go (single transaction (ACID))
    public void deleteResponsesByEmployeeAndSurvey(UUID employeeId, UUID surveyId) {
        responseRepository.deleteByEmployeeIdAndSurveyId(employeeId, surveyId);
    }
}