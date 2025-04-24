package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private UUID employeeId;
    private String name;
    private boolean isAdmin;
}
