package com.example.assessment.repository;

import com.example.assessment.model.DeptManager;
import com.example.assessment.model.DeptManagerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeptManagerRepository extends JpaRepository<DeptManager, DeptManagerId> {

    List<DeptManager> findByEmpNo(Integer empNo);

    List<DeptManager> findByDeptNo(String deptNo);

    @Query("SELECT dm FROM DeptManager dm WHERE dm.empNo = :empNo AND dm.toDate >= CURRENT_DATE")
    List<DeptManager> findCurrentManagedDepartments(@Param("empNo") Integer empNo);

    @Query("SELECT dm FROM DeptManager dm WHERE dm.deptNo = :deptNo AND dm.toDate >= CURRENT_DATE")
    Optional<DeptManager> findCurrentManagerOfDepartment(@Param("deptNo") String deptNo);

    @Query("SELECT CASE WHEN COUNT(dm) > 0 THEN true ELSE false END " +
            "FROM DeptManager dm WHERE dm.empNo = :empNo AND dm.toDate >= CURRENT_DATE")
    boolean isCurrentManager(@Param("empNo") Integer empNo);
}
