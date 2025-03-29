package org.example.controller;

import org.example.model.Question;
import org.example.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/surveys/{surveyId}/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<Question> createQuestion(@PathVariable UUID surveyId, @RequestBody Question question) {
        return ResponseEntity.ok(questionService.createQuestion(surveyId, question));
    }

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions(@PathVariable UUID surveyId) {
        return ResponseEntity.ok(questionService.getAllQuestions(surveyId));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable UUID surveyId, @PathVariable UUID questionId) {
        return ResponseEntity.ok(questionService.getQuestionById(surveyId, questionId));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID surveyId, @PathVariable UUID questionId) {
        questionService.deleteQuestion(surveyId, questionId);
        return ResponseEntity.noContent().build();
    }
}
