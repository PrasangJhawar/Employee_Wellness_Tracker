package org.example.repository;

import org.example.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ResponseRepository extends JpaRepository<Response, UUID> {

    List<Response> findBySurveyId(UUID surveyId); // getting responses for a specific survey

    List<Response> findByEmployeeId(UUID employeeId); // getting responses for a specific employee
}
