package com.example.assessment.repository;

import com.example.assessment.model.Salary;
import com.example.assessment.model.SalaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, SalaryId> {

    List<Salary> findByEmpNo(Integer empNo);

    List<Salary> findByEmpNoOrderByFromDateDesc(Integer empNo);


}
