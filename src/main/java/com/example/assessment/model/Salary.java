package com.example.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@IdClass(SalaryId.class)
@Entity
@Table(name = "salaries")
public class Salary {

    @Id
    @Column(name = "emp_no")
    private Integer empNo;

    @Id
    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(nullable = false)
    private Integer salary;

    @Column(name = "to_date")
    private LocalDate toDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_no", insertable = false, updatable = false)
    private Employee employee;
}


