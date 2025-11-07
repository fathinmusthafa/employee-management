package com.example.assessment.dto;

import com.example.assessment.model.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {

    @NotNull(message = "Employee number is required")
    @Positive(message = "Employee number must be positive")
    private Integer empNo;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "First name is required")
    @Size(max = 14, message = "First name must not exceed 14 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 16, message = "Last name must not exceed 16 characters")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "Hire date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;
}
