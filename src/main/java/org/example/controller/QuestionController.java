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

    //dependency injection
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    //creating a question
    @PostMapping
    public ResponseEntity<Question> createQuestion(@PathVariable UUID surveyId, @RequestBody Question question) {
        return ResponseEntity.ok(questionService.createQuestion(surveyId, question));
    }

    //fetching all questions
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions(@PathVariable UUID surveyId) {
        return ResponseEntity.ok(questionService.getAllQuestions(surveyId));
    }


    //fetching question by question id
    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable UUID surveyId, @PathVariable UUID questionId) {
        return ResponseEntity.ok(questionService.getQuestionById(surveyId, questionId));
    }

    //deleting a question
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID surveyId, @PathVariable UUID questionId) {
        questionService.deleteQuestion(surveyId, questionId);
        return ResponseEntity.noContent().build();
    }
}