package com.example.assessment.repository;


import com.example.assessment.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {


    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> searchByName(@Param("name") String name);

    // Stored Procedure calls
    @Procedure(procedureName = "sp_insert_employee")
    void insertEmployeeProcedure(
            @Param("p_emp_no") Integer empNo,
            @Param("p_birth_date") java.sql.Date birthDate,
            @Param("p_first_name") String firstName,
            @Param("p_last_name") String lastName,
            @Param("p_gender") String gender,
            @Param("p_hire_date") java.sql.Date hireDate
    );

    @Procedure(procedureName = "sp_update_employee")
    void updateEmployeeProcedure(
            @Param("p_emp_no") Integer empNo,
            @Param("p_birth_date") LocalDate birthDate,
            @Param("p_first_name") String firstName,
            @Param("p_last_name") String lastName,
            @Param("p_gender") String gender,
            @Param("p_hire_date") LocalDate hireDate
    );

    @Procedure(procedureName = "sp_delete_employee")
    void deleteEmployeeProcedure(@Param("p_emp_no") Integer empNo);
}
