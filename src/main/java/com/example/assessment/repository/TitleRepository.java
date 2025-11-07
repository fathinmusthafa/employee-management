package com.example.assessment.repository;

import com.example.assessment.model.Title;
import com.example.assessment.model.TitleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, TitleId> {
    List<Title> findByEmpNo(Integer empNo);

    List<Title> findByEmpNoOrderByFromDateDesc(Integer empNo);

    @Query("SELECT t FROM Title t WHERE t.empNo = :empNo AND (t.toDate IS NULL OR t.toDate >= CURRENT_DATE)")
    Optional<Title> findCurrentTitle(@Param("empNo") Integer empNo);

    List<Title> findByTitle(String title);

    @Query("SELECT t FROM Title t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Title> searchByTitle(@Param("title") String title);
}
