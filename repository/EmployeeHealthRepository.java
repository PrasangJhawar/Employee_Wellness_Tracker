package org.example.repository;

import org.example.model.EmployeeHealth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeHealthRepository extends JpaRepository<EmployeeHealth, Long> {
     List<EmployeeHealth> findByEmployeeIdAndSurveyId(UUID employeeId, UUID surveyId);
}
