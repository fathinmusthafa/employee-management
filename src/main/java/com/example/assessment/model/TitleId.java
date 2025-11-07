package com.example.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitleId implements java.io.Serializable {
    private Integer empNo;
    private LocalDate fromDate;
}
