package com.example.assessment.repository;

import com.example.assessment.model.DeptEmp;
import com.example.assessment.model.DeptEmpId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeptEmpRepository extends JpaRepository<DeptEmp, DeptEmpId> {

    List<DeptEmp> findByEmpNo(Integer empNo);

    List<DeptEmp> findByDeptNo(String deptNo);

    @Query("SELECT de FROM DeptEmp de WHERE de.empNo = :empNo AND de.toDate >= CURRENT_DATE")
    List<DeptEmp> findCurrentDepartments(@Param("empNo") Integer empNo);

    @Query("SELECT de FROM DeptEmp de WHERE de.deptNo = :deptNo AND de.toDate >= CURRENT_DATE")
    List<DeptEmp> findCurrentEmployeesInDepartment(@Param("deptNo") String deptNo);

    @Query("SELECT COUNT(de) FROM DeptEmp de WHERE de.deptNo = :deptNo AND de.toDate >= CURRENT_DATE")
    Long countEmployeesInDepartment(@Param("deptNo") String deptNo);
}
