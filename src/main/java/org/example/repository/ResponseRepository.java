package org.example.repository;

import org.example.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResponseRepository extends JpaRepository<Response, UUID> {
    List<Response> findBySurveyId(UUID surveyId);
    List<Response> findByEmployeeId(UUID employeeId);
    List<Response> findBySurveyIdAndEmployeeId(UUID surveyId, UUID employeeId);
}