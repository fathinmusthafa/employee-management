package com.example.assessment.repository;

import com.example.assessment.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    Optional<Department> findByDeptName(String deptName);

    boolean existsByDeptName(String deptName);

    @Query("SELECT d FROM Department d WHERE LOWER(d.deptName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Department> searchByName(@Param("name") String name);
}
