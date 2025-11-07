package com.example.assessment.service;

import com.example.assessment.dto.EmployeeDTO;
import com.example.assessment.exception.ResourceAlreadyExistException;
import com.example.assessment.exception.ResourceNotFoundException;
import com.example.assessment.model.*;
import com.example.assessment.repository.EmployeeRepository;
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
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<EmployeeDTO> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeById(Integer id) {
        log.info("Fetching employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return convertToDTO(employee);
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        log.info("Creating new employee: {}", employeeDTO);

        if (employeeRepository.existsById(employeeDTO.getEmpNo())) {
            throw new ResourceAlreadyExistException("Employee with id " + employeeDTO.getEmpNo() + " already exists");
        }

        Employee employee = convertToEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with id: {}", savedEmployee.getEmpNo());

        return convertToDTO(savedEmployee);
    }

    public EmployeeDTO updateEmployee(Integer id, EmployeeDTO employeeDTO) {
        log.info("Updating employee with id: {}", id);

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        existingEmployee.setBirthDate(employeeDTO.getBirthDate());
        existingEmployee.setFirstName(employeeDTO.getFirstName());
        existingEmployee.setLastName(employeeDTO.getLastName());
        existingEmployee.setGender(employeeDTO.getGender());
        existingEmployee.setHireDate(employeeDTO.getHireDate());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        log.info("Employee updated successfully with id: {}", updatedEmployee.getEmpNo());

        return convertToDTO(updatedEmployee);
    }

    public void deleteEmployee(Integer id) {
        log.info("Deleting employee with id: {}", id);

        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }

        employeeRepository.deleteById(id);
        log.info("Employee deleted successfully with id: {}", id);
    }

    public List<EmployeeDTO> searchEmployeesByName(String name) {
        log.info("Searching employees by name: {}", name);
        return employeeRepository.searchByName(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Using Stored Procedure
    public void createEmployeeViaProcedure(EmployeeDTO employeeDTO) {
        log.info("Creating employee via stored procedure: {}", employeeDTO);
        employeeRepository.insertEmployeeProcedure(
                employeeDTO.getEmpNo(),
                java.sql.Date.valueOf(employeeDTO.getBirthDate()),
                employeeDTO.getFirstName(),
                employeeDTO.getLastName(),
                employeeDTO.getGender().name(),
                java.sql.Date.valueOf(employeeDTO.getHireDate())
        );
        log.info("Employee created via stored procedure with id: {}", employeeDTO.getEmpNo());
    }

    public void updateEmployeeViaProcedure(Integer id, EmployeeDTO employeeDTO) {
        log.info("Updating employee via stored procedure with id: {}", id);
        employeeRepository.updateEmployeeProcedure(
                id,
                employeeDTO.getBirthDate(),
                employeeDTO.getFirstName(),
                employeeDTO.getLastName(),
                employeeDTO.getGender().name(),
                employeeDTO.getHireDate()
        );
        log.info("Employee updated via stored procedure with id: {}", id);
    }

    public void deleteEmployeeViaProcedure(Integer id) {
        log.info("Deleting employee via stored procedure with id: {}", id);
        employeeRepository.deleteEmployeeProcedure(id);
        log.info("Employee deleted via stored procedure with id: {}", id);
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .empNo(employee.getEmpNo())
                .birthDate(employee.getBirthDate())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .gender(employee.getGender())
                .hireDate(employee.getHireDate())
                .build();
    }

    private Employee convertToEntity(EmployeeDTO dto) {
        return Employee.builder()
                .empNo(dto.getEmpNo())
                .birthDate(dto.getBirthDate())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .gender(dto.getGender())
                .hireDate(dto.getHireDate())
                .build();
    }

}
