package com.example.assessment.service;

import com.example.assessment.dto.DepartmentDTO;
import com.example.assessment.exception.ResourceAlreadyExistException;
import com.example.assessment.exception.ResourceNotFoundException;
import com.example.assessment.model.Department;
import com.example.assessment.repository.DepartmentRepository;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentDTO> getAllDepartments() {
        log.info("Fetching all departments");
        return departmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO getDepartmentById(String id) {
        log.info("Fetching department with id: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return convertToDTO(department);
    }

    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        log.info("Creating new department: {}", departmentDTO);

        if (departmentRepository.existsById(departmentDTO.getDeptNo())) {
            throw new ResourceAlreadyExistException("Department with id " + departmentDTO.getDeptNo() + " already exists");
        }

        if (departmentRepository.existsByDeptName(departmentDTO.getDeptName())) {
            throw new ResourceAlreadyExistException("Department with name " + departmentDTO.getDeptName() + " already exists");
        }

        Department department = convertToEntity(departmentDTO);
        Department savedDepartment = departmentRepository.save(department);
        log.info("Department created successfully with id: {}", savedDepartment.getDeptNo());

        return convertToDTO(savedDepartment);
    }

    public DepartmentDTO updateDepartment(String id, DepartmentDTO departmentDTO) {
        log.info("Updating department with id: {}", id);

        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        existingDepartment.setDeptName(departmentDTO.getDeptName());

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        log.info("Department updated successfully with id: {}", updatedDepartment.getDeptNo());

        return convertToDTO(updatedDepartment);
    }

    public void deleteDepartment(String id) {
        log.info("Deleting department with id: {}", id);

        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id: " + id);
        }

        departmentRepository.deleteById(id);
        log.info("Department deleted successfully with id: {}", id);
    }

    public List<DepartmentDTO> searchDepartmentsByName(String name) {
        log.info("Searching departments by name: {}", name);
        return departmentRepository.searchByName(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DepartmentDTO convertToDTO(Department department) {
        return DepartmentDTO.builder()
                .deptNo(department.getDeptNo())
                .deptName(department.getDeptName())
                .build();
    }

    private Department convertToEntity(DepartmentDTO dto) {
        return Department.builder()
                .deptNo(dto.getDeptNo())
                .deptName(dto.getDeptName())
                .build();
    }
}
