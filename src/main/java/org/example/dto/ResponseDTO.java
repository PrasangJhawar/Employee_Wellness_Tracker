package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ResponseDTO {
    private UUID id;
    private String responseText;
    private UUID employeeId;
    private UUID questionId;
    private String questionText;
    private UUID surveyId;
    private String surveyTitle;
    private String surveyDescription;
}
