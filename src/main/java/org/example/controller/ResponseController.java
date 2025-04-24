package org.example.controller;

import org.example.dto.ResponseDTO;
import org.example.service.ResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/responses")
public class ResponseController {

    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping("/submit")
    public ResponseEntity<ResponseDTO> submitResponse(@RequestBody ResponseDTO responseDTO) {
        ResponseDTO savedResponse = responseService.submitResponse(
                responseDTO.getEmployeeId(),
                responseDTO.getSurveyId(),
                responseDTO.getQuestionId(),
                responseDTO.getResponseText()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(savedResponse);
    }

    @PostMapping("/surveys/{surveyId}/questions/{questionId}/responses")
    public ResponseEntity<ResponseDTO> submitResponse(
            @PathVariable UUID surveyId,
            @PathVariable UUID questionId,
            @RequestBody ResponseDTO responseDTO) {

        ResponseDTO savedResponse = responseService.submitResponse(
                responseDTO.getEmployeeId(),
                surveyId,
                questionId,
                responseDTO.getResponseText()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(savedResponse);
    }

    @GetMapping("/survey/{surveyId}")
    public ResponseEntity<List<ResponseDTO>> getResponsesForSurvey(@PathVariable UUID surveyId) {
        return ResponseEntity.ok(responseService.getResponsesForSurvey(surveyId));
    }

    //dashboard.js (fetchSurveys and fetchSubmittedSurveys)
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ResponseDTO>> getResponsesForEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(responseService.getResponsesForEmployee(employeeId));
    }

    //used in fetchSubmittedResponses() in submitted-responses.js
    @GetMapping("/survey/{surveyId}/employee/{employeeId}")
    public ResponseEntity<List<ResponseDTO>> getResponsesForSurveyByEmployee(
            @PathVariable UUID surveyId, @PathVariable UUID employeeId) {
        return ResponseEntity.ok(responseService.getResponsesForSurveyByEmployee(surveyId, employeeId));
    }

    //used in dashboard.js to delete responses
    @DeleteMapping("/delete/{employeeId}/{surveyId}")
    public ResponseEntity<Void> deleteEmployeeResponses(@PathVariable UUID employeeId, @PathVariable UUID surveyId) {
        responseService.deleteResponsesByEmployeeAndSurvey(employeeId, surveyId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{responseId}")
    public ResponseEntity<ResponseDTO> editResponse(
            @PathVariable UUID responseId,
            @RequestBody Map<String, String> requestBody) {

        String newResponseText = requestBody.get("text");

        ResponseDTO updated = responseService.editResponse(responseId, newResponseText);
        return ResponseEntity.ok(updated);
    }



}
