package com.example.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDTO {

    @NotBlank(message = "Department number is required")
    @Size(min = 4, max = 4, message = "Department number must be 4 characters")
    private String deptNo;

    @NotBlank(message = "Department name is required")
    @Size(max = 40, message = "Department name must not exceed 40 characters")
    private String deptName;

}
