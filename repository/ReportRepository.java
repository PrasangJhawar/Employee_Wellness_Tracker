package org.example.repository;

import org.example.model.EmployeeHealth;
import org.example.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
}