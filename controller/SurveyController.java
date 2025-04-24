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
        return survey.map(ResponseEntity::ok)//works if survey present in optional
                .orElseGet(() -> ResponseEntity.notFound().build());//builds status code 404
    }


    // Delete a survey
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable UUID id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();//code 204 (operation successful but nothing to return)
    }
}