package com.example.assessment.service;

import com.example.assessment.dto.DeptEmpDTO;
import com.example.assessment.exception.ResourceAlreadyExistException;
import com.example.assessment.exception.ResourceNotFoundException;
import com.example.assessment.model.DeptEmp;
import com.example.assessment.model.DeptEmpId;
import com.example.assessment.repository.DepartmentRepository;
import com.example.assessment.repository.DeptEmpRepository;
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
public class DeptEmpService {

    private final DeptEmpRepository deptEmpRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public List<DeptEmpDTO> getAllDeptEmps() {
        log.info("Fetching all department-employee relationships");
        return deptEmpRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeptEmpDTO> getDepartmentsByEmployee(Integer empNo) {
        log.info("Fetching departments for employee: {}", empNo);

        if (!employeeRepository.existsById(empNo)) {
            throw new ResourceNotFoundException("Employee not found with id: " + empNo);
        }

        return deptEmpRepository.findByEmpNo(empNo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<DeptEmpDTO> getCurrentDepartmentsByEmployee(Integer empNo) {
        log.info("Fetching current departments for employee: {}", empNo);

        if (!employeeRepository.existsById(empNo)) {
            throw new ResourceNotFoundException("Employee not found with id: " + empNo);
        }

        return deptEmpRepository.findCurrentDepartments(empNo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<DeptEmpDTO> getEmployeesByDepartment(String deptNo) {
        log.info("Fetching employees in department: {}", deptNo);

        if (!departmentRepository.existsById(deptNo)) {
            throw new ResourceNotFoundException("Department not found with id: " + deptNo);
        }

        return deptEmpRepository.findByDeptNo(deptNo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<DeptEmpDTO> getCurrentEmployeesInDepartment(String deptNo) {
        log.info("Fetching current employees in department: {}", deptNo);

        if (!departmentRepository.existsById(deptNo)) {
            throw new ResourceNotFoundException("Department not found with id: " + deptNo);
        }

        return deptEmpRepository.findCurrentEmployeesInDepartment(deptNo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    public DeptEmpDTO addEmployeeToDepartment(DeptEmpDTO deptEmpDTO) {
        log.info("Assigning employee to department: {}", deptEmpDTO);


        if (!employeeRepository.existsById(deptEmpDTO.getEmpNo())) {
            throw new ResourceNotFoundException("Employee not found with id: " + deptEmpDTO.getEmpNo());
        }

        if (!departmentRepository.existsById(deptEmpDTO.getDeptNo())) {
            throw new ResourceNotFoundException("Department not found with id: " + deptEmpDTO.getDeptNo());
        }

        DeptEmpId id = new DeptEmpId(deptEmpDTO.getEmpNo(), deptEmpDTO.getDeptNo());
        if (deptEmpRepository.existsById(id)) {
            throw new ResourceAlreadyExistException("Employee already assigned to this department");
        }

        DeptEmp deptEmp = convertToEntity(deptEmpDTO);
        DeptEmp saved = deptEmpRepository.save(deptEmp);
        log.info("Employee assigned to department successfully");

        return convertToDTO(saved);
    }


    public DeptEmpDTO updateDeptEmp(Integer empNo, String deptNo, DeptEmpDTO deptEmpDTO) {
        log.info("Updating dept-emp for employee: {} dept: {}", empNo, deptNo);

        DeptEmpId id = new DeptEmpId(empNo, deptNo);
        DeptEmp existing = deptEmpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department assignment not found"));

        existing.setFromDate(deptEmpDTO.getFromDate());
        existing.setToDate(deptEmpDTO.getToDate());

        DeptEmp updated = deptEmpRepository.save(existing);
        log.info("Department assignment updated successfully");

        return convertToDTO(updated);
    }

    public void deleteEmployeeFromDepartment(Integer empNo, String deptNo) {
        log.info("Removing employee: {} from department: {}", empNo, deptNo);

        DeptEmpId id = new DeptEmpId(empNo, deptNo);

        if (!deptEmpRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department assignment not found");
        }

        deptEmpRepository.deleteById(id);
        log.info("Employee removed from department successfully");
    }

    private DeptEmpDTO convertToDTO(DeptEmp deptEmp) {
        return DeptEmpDTO.builder()
                .empNo(deptEmp.getEmpNo())
                .deptNo(deptEmp.getDeptNo())
                .fromDate(deptEmp.getFromDate())
                .toDate(deptEmp.getToDate())
                .build();
    }

    private DeptEmp convertToEntity(DeptEmpDTO dto) {
        return DeptEmp.builder()
                .empNo(dto.getEmpNo())
                .deptNo(dto.getDeptNo())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .build();
    }
}
