package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reportName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false)
    private String generatedBy; //admin who generated the report


    //for filters
    private String departmentFilter;
    private String locationFilter;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    //lob stores large object
    @Lob
    private String reportData; //JSON or CSV content

    private String surveyType;
    private String surveyResult;

    //this tells method to be called before the things get saved
    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }
}