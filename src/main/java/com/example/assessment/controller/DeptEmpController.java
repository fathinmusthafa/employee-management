package com.example.assessment.controller;

import com.example.assessment.dto.DeptEmpDTO;
import com.example.assessment.service.DeptEmpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/dept-emp")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Department-Employee Management", description = "APIs for managing employee department assignments")
public class DeptEmpController {

    private final DeptEmpService deptEmpService;

    @GetMapping
    @Operation(summary = "Get all department assignments", description = "Retrieve all employee-department relationships")
    public ResponseEntity<CollectionModel<EntityModel<DeptEmpDTO>>> getAllDeptEmps() {
        log.info("GET /api/dept-emp - Fetching all department assignments");

        List<EntityModel<DeptEmpDTO>> deptEmps = deptEmpService.getAllDeptEmps().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DeptEmpDTO>> collectionModel = CollectionModel.of(deptEmps,
                linkTo(methodOn(DeptEmpController.class).getAllDeptEmps()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/employee/{empNo}")
    @Operation(summary = "Get departments by employee", description = "Get all department assignments for an employee")
    public ResponseEntity<CollectionModel<EntityModel<DeptEmpDTO>>> getDepartmentsByEmployee(
            @PathVariable Integer empNo) {
        log.info("GET /api/dept-emp/employee/{} - Fetching departments", empNo);

        List<EntityModel<DeptEmpDTO>> deptEmps = deptEmpService.getDepartmentsByEmployee(empNo).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DeptEmpDTO>> collectionModel = CollectionModel.of(deptEmps,
                linkTo(methodOn(DeptEmpController.class).getDepartmentsByEmployee(empNo)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).getEmployeeById(empNo)).withRel("employee"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/employee/{empNo}/current")
    @Operation(summary = "Get current departments", description = "Get current department assignments for an employee")
    public ResponseEntity<CollectionModel<EntityModel<DeptEmpDTO>>> getCurrentDepartmentsByEmployee(
            @PathVariable Integer empNo) {
        log.info("GET /api/dept-emp/employee/{}/current - Fetching current departments", empNo);

        List<EntityModel<DeptEmpDTO>> deptEmps = deptEmpService.getCurrentDepartmentsByEmployee(empNo).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DeptEmpDTO>> collectionModel = CollectionModel.of(deptEmps,
                linkTo(methodOn(DeptEmpController.class).getCurrentDepartmentsByEmployee(empNo)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/department/{deptNo}")
    @Operation(summary = "Get employees by department", description = "Get all employees in a department")
    public ResponseEntity<CollectionModel<EntityModel<DeptEmpDTO>>> getEmployeesByDepartment(
            @PathVariable String deptNo) {
        log.info("GET /api/dept-emp/department/{} - Fetching employees", deptNo);

        List<EntityModel<DeptEmpDTO>> deptEmps = deptEmpService.getEmployeesByDepartment(deptNo).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DeptEmpDTO>> collectionModel = CollectionModel.of(deptEmps,
                linkTo(methodOn(DeptEmpController.class).getEmployeesByDepartment(deptNo)).withSelfRel(),
                linkTo(methodOn(DepartmentController.class).getDepartmentById(deptNo)).withRel("department"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/department/{deptNo}/current")
    @Operation(summary = "Get current employees", description = "Get current employees in a department")
    public ResponseEntity<CollectionModel<EntityModel<DeptEmpDTO>>> getCurrentEmployeesInDepartment(
            @PathVariable String deptNo) {
        log.info("GET /api/dept-emp/department/{}/current - Fetching current employees", deptNo);

        List<EntityModel<DeptEmpDTO>> deptEmps = deptEmpService.getCurrentEmployeesInDepartment(deptNo).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DeptEmpDTO>> collectionModel = CollectionModel.of(deptEmps,
                linkTo(methodOn(DeptEmpController.class).getCurrentEmployeesInDepartment(deptNo)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping
    @Operation(summary = "Assign employee to department", description = "Create new department assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Assignment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Employee or Department not found")
    })
    public ResponseEntity<EntityModel<DeptEmpDTO>> assignEmployeeToDepartment(
            @Valid @RequestBody DeptEmpDTO deptEmpDTO) {
        log.info("POST /api/dept-emp - Assigning employee to department");

        DeptEmpDTO created = deptEmpService.addEmployeeToDepartment(deptEmpDTO);
        EntityModel<DeptEmpDTO> model = toModel(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{empNo}/{deptNo}")
    @Operation(summary = "Update assignment", description = "Update department assignment dates")
    public ResponseEntity<EntityModel<DeptEmpDTO>> updateDeptEmp(
            @PathVariable Integer empNo,
            @PathVariable String deptNo,
            @Valid @RequestBody DeptEmpDTO deptEmpDTO) {
        log.info("PUT /api/dept-emp/{}/{} - Updating assignment", empNo, deptNo);

        DeptEmpDTO updated = deptEmpService.updateDeptEmp(empNo, deptNo, deptEmpDTO);
        EntityModel<DeptEmpDTO> model = toModel(updated);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{empNo}/{deptNo}")
    @Operation(summary = "Remove assignment", description = "Remove employee from department")
    public ResponseEntity<Void> removeEmployeeFromDepartment(
            @PathVariable Integer empNo,
            @PathVariable String deptNo) {
        log.info("DELETE /api/dept-emp/{}/{} - Removing assignment", empNo, deptNo);

        deptEmpService.deleteEmployeeFromDepartment(empNo, deptNo);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<DeptEmpDTO> toModel(DeptEmpDTO deptEmp) {
        return EntityModel.of(deptEmp,
                linkTo(methodOn(DeptEmpController.class)
                        .getDepartmentsByEmployee(deptEmp.getEmpNo())).withRel("employee-departments"),
                linkTo(methodOn(DeptEmpController.class)
                        .getEmployeesByDepartment(deptEmp.getDeptNo())).withRel("department-employees"),
                linkTo(methodOn(EmployeeController.class)
                        .getEmployeeById(deptEmp.getEmpNo())).withRel("employee"),
                linkTo(methodOn(DepartmentController.class)
                        .getDepartmentById(deptEmp.getDeptNo())).withRel("department"));
    }
}
