package com.example.assessment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeptManagerDTO {

    @NotNull(message = "Employee number is required")
    @Positive(message = "Employee number must be positive")
    private Integer empNo;

    @NotBlank(message = "Department number is required")
    @Size(min = 4, max = 4, message = "Department number must be 4 characters")
    private String deptNo;

    @NotNull(message = "From date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;
}
