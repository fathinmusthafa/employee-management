package com.example.assessment.service;

import com.example.assessment.dto.DeptManagerDTO;
import com.example.assessment.exception.ResourceAlreadyExistException;
import com.example.assessment.exception.ResourceNotFoundException;
import com.example.assessment.model.DeptManager;
import com.example.assessment.model.DeptManagerId;
import com.example.assessment.repository.DepartmentRepository;
import com.example.assessment.repository.DeptManagerRepository;
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
public class DeptManagerService {

    private final DeptManagerRepository deptManagerRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public List<DeptManagerDTO> getAllDeptManagers() {
        log.info("Fetching all department managers");
        return deptManagerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeptManagerDTO> getDepartmentsManagedByEmployee(Integer empNo) {
        log.info("Fetching departments managed by employee: {}", empNo);

        if (!employeeRepository.existsById(empNo)) {
            throw new ResourceNotFoundException("Employee not found with id: " + empNo);
        }

        return deptManagerRepository.findByEmpNo(empNo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeptManagerDTO> getCurrentDepartmentsManagedByEmployee(Integer empNo) {
        log.info("Fetching current departments managed by employee: {}", empNo);

        if (!employeeRepository.existsById(empNo)) {
            throw new ResourceNotFoundException("Employee not found with id: " + empNo);
        }

        return deptManagerRepository.findCurrentManagedDepartments(empNo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeptManagerDTO> getManagersOfDepartment(String deptNo) {
        log.info("Fetching managers of department: {}", deptNo);

        if (!departmentRepository.existsById(deptNo)) {
            throw new ResourceNotFoundException("Department not found with id: " + deptNo);
        }

        return deptManagerRepository.findByDeptNo(deptNo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public DeptManagerDTO getCurrentManagerOfDepartment(String deptNo) {
        log.info("Fetching current manager of department: {}", deptNo);

        if (!departmentRepository.existsById(deptNo)) {
            throw new ResourceNotFoundException("Department not found with id: " + deptNo);
        }

        DeptManager manager = deptManagerRepository.findCurrentManagerOfDepartment(deptNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No current manager found for department: " + deptNo));

        return convertToDTO(manager);
    }


    public boolean isCurrentManager(Integer empNo) {
        log.info("Checking if employee {} is current manager", empNo);

        if (!employeeRepository.existsById(empNo)) {
            throw new ResourceNotFoundException("Employee not found with id: " + empNo);
        }

        return deptManagerRepository.isCurrentManager(empNo);
    }


    public DeptManagerDTO addManagerToDepartment(DeptManagerDTO deptManagerDTO) {
        log.info("Assigning manager to department: {}", deptManagerDTO);

        if (!employeeRepository.existsById(deptManagerDTO.getEmpNo())) {
            throw new ResourceNotFoundException("Employee not found with id: " + deptManagerDTO.getEmpNo());
        }

        if (!departmentRepository.existsById(deptManagerDTO.getDeptNo())) {
            throw new ResourceNotFoundException("Department not found with id: " + deptManagerDTO.getDeptNo());
        }

        DeptManagerId id = new DeptManagerId(deptManagerDTO.getEmpNo(), deptManagerDTO.getDeptNo());
        if (deptManagerRepository.existsById(id)) {
            throw new ResourceAlreadyExistException("Manager already assigned to this department");
        }

        DeptManager deptManager = convertToEntity(deptManagerDTO);
        DeptManager saved = deptManagerRepository.save(deptManager);
        log.info("Manager assigned to department successfully");

        return convertToDTO(saved);
    }

    public DeptManagerDTO updateDeptManager(Integer empNo, String deptNo, DeptManagerDTO deptManagerDTO) {
        log.info("Updating dept-manager for employee: {} dept: {}", empNo, deptNo);

        DeptManagerId id = new DeptManagerId(empNo, deptNo);
        DeptManager existing = deptManagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager assignment not found"));

        existing.setFromDate(deptManagerDTO.getFromDate());
        existing.setToDate(deptManagerDTO.getToDate());

        DeptManager updated = deptManagerRepository.save(existing);
        log.info("Manager assignment updated successfully");

        return convertToDTO(updated);
    }

    public void deleteManagerFromDepartment(Integer empNo, String deptNo) {
        log.info("Removing manager: {} from department: {}", empNo, deptNo);

        DeptManagerId id = new DeptManagerId(empNo, deptNo);

        if (!deptManagerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manager assignment not found");
        }

        deptManagerRepository.deleteById(id);
        log.info("Manager removed from department successfully");
    }

    private DeptManagerDTO convertToDTO(DeptManager deptManager) {
        return DeptManagerDTO.builder()
                .empNo(deptManager.getEmpNo())
                .deptNo(deptManager.getDeptNo())
                .fromDate(deptManager.getFromDate())
                .toDate(deptManager.getToDate())
                .build();
    }

    private DeptManager convertToEntity(DeptManagerDTO dto) {
        return DeptManager.builder()
                .empNo(dto.getEmpNo())
                .deptNo(dto.getDeptNo())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .build();
    }
}
