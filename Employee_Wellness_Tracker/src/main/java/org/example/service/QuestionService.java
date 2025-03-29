package org.example.service;

import org.example.model.Question;
import org.example.model.Survey;
import org.example.repository.QuestionRepository;
import org.example.repository.SurveyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SurveyRepository surveyRepository;

    public QuestionService(QuestionRepository questionRepository, SurveyRepository surveyRepository) {
        this.questionRepository = questionRepository;
        this.surveyRepository = surveyRepository;
    }

    public Question createQuestion(UUID surveyId, Question question) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        question.setSurvey(survey);
        return questionRepository.save(question);
    }

    public List<Question> getAllQuestions(UUID surveyId) {
        return questionRepository.findBySurveyId(surveyId);
    }

    public Question getQuestionById(UUID surveyId, UUID questionId) {
        return questionRepository.findBySurveyIdAndId(surveyId, questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public void deleteQuestion(UUID surveyId, UUID questionId) {
        questionRepository.deleteById(questionId);
    }
}
