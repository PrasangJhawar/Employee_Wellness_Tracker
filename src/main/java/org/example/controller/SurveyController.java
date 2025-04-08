package org.example.controller;

import org.example.model.Survey;
import org.example.service.SurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/surveys")
public class SurveyController {
    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    // Create a new survey
    @PostMapping
    public ResponseEntity<Survey> createSurvey(@RequestBody Survey survey) {
        return ResponseEntity.ok(surveyService.createSurvey(survey));
    }

    // get all surveys
    @GetMapping
    public ResponseEntity<List<Survey>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }

    // get survey by id
    @GetMapping("/{id}")
    public ResponseEntity<Survey> getSurveyById(@PathVariable UUID id) {
        Optional<Survey> survey = surveyService.getSurveyById(id);
        return survey.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update survey (with admin check, since only admin can update)
    @PutMapping("/{id}")
    public ResponseEntity<Survey> updateSurvey(@PathVariable UUID id, @RequestBody Survey updatedSurvey, @RequestHeader UUID employeeId) {
        // admin check is done inside SurveyService
        try {
            Survey updated = surveyService.updateSurvey(id, updatedSurvey, employeeId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null); //denied if not an admin
        }
    }

    // Delete a survey
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable UUID id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }
}