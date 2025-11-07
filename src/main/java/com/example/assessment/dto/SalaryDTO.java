package com.example.assessment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryDTO {

    @NotNull(message = "Employee number is required")
    @Positive(message = "Employee number must be positive")
    private Integer empNo;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    private Integer salary;

    @NotNull(message = "From date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;
}
