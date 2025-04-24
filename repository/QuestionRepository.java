package org.example.repository;

import org.example.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findBySurveyId(UUID surveyId); // finds all questions for a specific survey
    Optional<Question> findBySurveyIdAndId(UUID surveyId, UUID questionId); // finds a specific question for a survey
}