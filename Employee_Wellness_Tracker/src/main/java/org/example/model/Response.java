package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee; // employee who answered the question

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey; // survey this response is part of

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // quesion being answered

    @Column(nullable = false)
    private String responseText; // answer provided by the employee

    @Column(nullable = false)
    private boolean isActive = true; // whether the response is active or deleted

    private long timestamp; // timestamp when the response was given
}
