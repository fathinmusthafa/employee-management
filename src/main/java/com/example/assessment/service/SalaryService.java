package com.example.assessment.service;

import com.example.assessment.dto.SalaryDTO;
import com.example.assessment.exception.ResourceNotFoundException;
import com.example.assessment.model.Salary;
import com.example.assessment.model.SalaryId;
import com.example.assessment.repository.EmployeeRepository;
import com.example.assessment.repository.SalaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    public List<SalaryDTO> getAllSalaries() {
        log.info("Fetching all salaries");
        return salaryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SalaryDTO> getSalariesByEmployee(Integer empNo) {
        log.info("Fetching salaries for employee: {}", empNo);
        return salaryRepository.findByEmpNoOrderByFromDateDesc(empNo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SalaryDTO createSalary(SalaryDTO salaryDTO) {
        log.info("Creating new salary: {}", salaryDTO);

        if (!employeeRepository.existsById(salaryDTO.getEmpNo())) {
            throw new ResourceNotFoundException("Employee not found with id: " + salaryDTO.getEmpNo());
        }

        Salary salary = convertToEntity(salaryDTO);
        Salary savedSalary = salaryRepository.save(salary);
        log.info("Salary created successfully");

        return convertToDTO(savedSalary);
    }

    public SalaryDTO updateSalary(Integer empNo, String fromDate, SalaryDTO salaryDTO) {
        log.info("Update salaryfor employee: {} from date: {}", empNo, fromDate);
        SalaryId id = new SalaryId(empNo, java.time.LocalDate.parse(fromDate));

        if (!salaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salary not found");
        }

        Salary salary = convertToEntity(salaryDTO);
        Salary savedSalary = salaryRepository.save(salary);
        log.info("Salary updated successfully");

        return convertToDTO(savedSalary);
    }

    public void deleteSalary(Integer empNo, String fromDate) {
        log.info("Deleting salary for employee: {} from date: {}", empNo, fromDate);
        SalaryId id = new SalaryId(empNo, java.time.LocalDate.parse(fromDate));

        if (!salaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salary not found");
        }

        salaryRepository.deleteById(id);
        log.info("Salary deleted successfully");
    }

    private SalaryDTO convertToDTO(Salary salary) {
        return SalaryDTO.builder()
                .empNo(salary.getEmpNo())
                .salary(salary.getSalary())
                .fromDate(salary.getFromDate())
                .toDate(salary.getToDate())
                .build();
    }

    private Salary convertToEntity(SalaryDTO dto) {
        return Salary.builder()
                .empNo(dto.getEmpNo())
                .salary(dto.getSalary())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .build();
    }
}
