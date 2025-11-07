package com.example.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @Column(name = "dept_no", length = 4)
    private String deptNo;

    @Column(name = "dept_name", nullable = false, length = 40, unique = true)
    private String deptName;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeptEmp> deptEmps;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeptManager> deptManagers;
}
