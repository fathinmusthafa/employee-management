package com.example.assessment.controller;

import com.example.assessment.dto.ApiResponseDTO;
import com.example.assessment.dto.DeptManagerDTO;
import com.example.assessment.service.DeptManagerService;
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
@RequestMapping("/api/dept-manager")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Department Manager Management", description = "APIs for managing department managers")
public class DeptManagerController {

    private final DeptManagerService deptManagerService;

    @GetMapping
    @Operation(summary = "Get all managers", description = "Retrieve all department manager assignments")
    public ResponseEntity<CollectionModel<EntityModel<DeptManagerDTO>>> getAllDeptManagers() {
        log.info("GET /api/dept-manager - Fetching all managers");

        List<EntityModel<DeptManagerDTO>> managers = deptManagerService.getAllDeptManagers().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DeptManagerDTO>> collectionModel = CollectionModel.of(managers,
                linkTo(methodOn(DeptManagerController.class).getAllDeptManagers()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/employee/{empNo}")
    @Operation(summary = "Get departments managed by employee", description = "Get all departments managed by an employee")
    public ResponseEntity<CollectionModel<EntityModel<DeptManagerDTO>>> getDepartmentsManagedByEmployee(
            @PathVariable Integer empNo) {
        log.info("GET /api/dept-manager/employee/{} - Fetching managed departments", empNo);

        List<EntityModel<DeptManagerDTO>> managers = deptManagerService.getDepartmentsManagedByEmployee(empNo).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DeptManagerDTO>> collectionModel = CollectionModel.of(managers,
                linkTo(methodOn(DeptManagerController.class).getDepartmentsManagedByEmployee(empNo)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).getEmployeeById(empNo)).withRel("employee"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/employee/{empNo}/current")
    @Operation(summary = "Get current departments managed", description = "Get current departments managed by an employee")
    public ResponseEntity<CollectionModel<EntityModel<DeptManagerDTO>>> getCurrentDepartmentsManagedByEmployee(
            @PathVariable Integer empNo) {
        log.info("GET /api/dept-manager/employee/{}/current - Fetching current managed departments", empNo);

        List<EntityModel<DeptManagerDTO>> managers = deptManagerService.getCurrentDepartmentsManagedByEmployee(empNo).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DeptManagerDTO>> collectionModel = CollectionModel.of(managers,
                linkTo(methodOn(DeptManagerController.class).getCurrentDepartmentsManagedByEmployee(empNo)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/department/{deptNo}")
    @Operation(summary = "Get managers of department", description = "Get all managers (history) of a department")
    public ResponseEntity<CollectionModel<EntityModel<DeptManagerDTO>>> getManagersOfDepartment(
            @PathVariable String deptNo) {
        log.info("GET /api/dept-manager/department/{} - Fetching managers", deptNo);

        List<EntityModel<DeptManagerDTO>> managers = deptManagerService.getManagersOfDepartment(deptNo).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DeptManagerDTO>> collectionModel = CollectionModel.of(managers,
                linkTo(methodOn(DeptManagerController.class).getManagersOfDepartment(deptNo)).withSelfRel(),
                linkTo(methodOn(DepartmentController.class).getDepartmentById(deptNo)).withRel("department"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/department/{deptNo}/current")
    @Operation(summary = "Get current manager", description = "Get current manager of a department")
    public ResponseEntity<EntityModel<DeptManagerDTO>> getCurrentManagerOfDepartment(
            @PathVariable String deptNo) {
        log.info("GET /api/dept-manager/department/{}/current - Fetching current manager", deptNo);

        DeptManagerDTO manager = deptManagerService.getCurrentManagerOfDepartment(deptNo);
        EntityModel<DeptManagerDTO> model = toModel(manager);

        return ResponseEntity.ok(model);
    }

    @GetMapping("/employee/{empNo}/is-manager")
    @Operation(summary = "Check if employee is manager", description = "Check if employee is currently a manager")
    public ResponseEntity<ApiResponseDTO<Boolean>> isCurrentManager(@PathVariable Integer empNo) {
        log.info("GET /api/dept-manager/employee/{}/is-manager - Checking manager status", empNo);

        boolean isManager = deptManagerService.isCurrentManager(empNo);

        return ResponseEntity.ok(ApiResponseDTO.success("Manager status retrieved", isManager));
    }

    @PostMapping
    @Operation(summary = "Assign manager", description = "Assign an employee as department manager")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Manager assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Employee or Department not found")
    })
    public ResponseEntity<EntityModel<DeptManagerDTO>> assignManagerToDepartment(
            @Valid @RequestBody DeptManagerDTO deptManagerDTO) {
        log.info("POST /api/dept-manager - Assigning manager to department");

        DeptManagerDTO created = deptManagerService.addManagerToDepartment(deptManagerDTO);
        EntityModel<DeptManagerDTO> model = toModel(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{empNo}/{deptNo}")
    @Operation(summary = "Update manager assignment", description = "Update manager assignment dates")
    public ResponseEntity<EntityModel<DeptManagerDTO>> updateDeptManager(
            @PathVariable Integer empNo,
            @PathVariable String deptNo,
            @Valid @RequestBody DeptManagerDTO deptManagerDTO) {
        log.info("PUT /api/dept-manager/{}/{} - Updating manager assignment", empNo, deptNo);

        DeptManagerDTO updated = deptManagerService.updateDeptManager(empNo, deptNo, deptManagerDTO);
        EntityModel<DeptManagerDTO> model = toModel(updated);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{empNo}/{deptNo}")
    @Operation(summary = "Remove manager", description = "Remove employee as department manager")
    public ResponseEntity<Void> removeManagerFromDepartment(
            @PathVariable Integer empNo,
            @PathVariable String deptNo) {
        log.info("DELETE /api/dept-manager/{}/{} - Removing manager", empNo, deptNo);

        deptManagerService.deleteManagerFromDepartment(empNo, deptNo);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<DeptManagerDTO> toModel(DeptManagerDTO deptManager) {
        return EntityModel.of(deptManager,
                linkTo(methodOn(DeptManagerController.class)
                        .getDepartmentsManagedByEmployee(deptManager.getEmpNo())).withRel("managed-departments"),
                linkTo(methodOn(DeptManagerController.class)
                        .getManagersOfDepartment(deptManager.getDeptNo())).withRel("department-managers"),
                linkTo(methodOn(EmployeeController.class)
                        .getEmployeeById(deptManager.getEmpNo())).withRel("employee"),
                linkTo(methodOn(DepartmentController.class)
                        .getDepartmentById(deptManager.getDeptNo())).withRel("department"));
    }
}
