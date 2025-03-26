package org.example.controller;

import org.example.model.Response;
import org.example.service.ResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/surveys/{surveyId}/questions/{questionId}/responses")
public class ResponseController {

    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping
    public ResponseEntity<Response> submitResponse(@PathVariable UUID surveyId,
                                                   @PathVariable UUID questionId,
                                                   @RequestBody String responseText,
                                                   @RequestHeader UUID employeeId) {
        return ResponseEntity.ok(responseService.submitResponse(employeeId, surveyId, questionId, responseText));
    }

    @GetMapping
    public ResponseEntity<List<Response>> getResponsesForSurvey(@PathVariable UUID surveyId) {
        return ResponseEntity.ok(responseService.getResponsesForSurvey(surveyId));
    }

    @DeleteMapping("/{responseId}")
    public ResponseEntity<Void> deleteResponse(@PathVariable UUID responseId) {
        responseService.deleteResponse(responseId);
        return ResponseEntity.noContent().build();
    }
}
