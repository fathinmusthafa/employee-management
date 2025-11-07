package com.example.assessment.controller;

import com.example.assessment.dto.DepartmentDTO;
import com.example.assessment.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Department Management", description = "APIs for managing departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<CollectionModel<EntityModel<DepartmentDTO>>> getAllDepartments() {
        log.info("GET /api/departments - Fetching all departments");

        List<EntityModel<DepartmentDTO>> departments = departmentService.getAllDepartments().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DepartmentDTO>> collectionModel = CollectionModel.of(departments,
                linkTo(methodOn(DepartmentController.class).getAllDepartments()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<EntityModel<DepartmentDTO>> getDepartmentById(@PathVariable String id) {
        log.info("GET /api/departments/{} - Fetching department", id);

        DepartmentDTO department = departmentService.getDepartmentById(id);
        EntityModel<DepartmentDTO> model = toModel(department);

        return ResponseEntity.ok(model);
    }

    @PostMapping
    @Operation(summary = "Create new department")
    public ResponseEntity<EntityModel<DepartmentDTO>> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("POST /api/departments - Creating new department");

        DepartmentDTO created = departmentService.createDepartment(departmentDTO);
        EntityModel<DepartmentDTO> model = toModel(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department")
    public ResponseEntity<EntityModel<DepartmentDTO>> updateDepartment(
            @PathVariable String id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("PUT /api/departments/{} - Updating department", id);

        DepartmentDTO updated = departmentService.updateDepartment(id, departmentDTO);
        EntityModel<DepartmentDTO> model = toModel(updated);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String id) {
        log.info("DELETE /api/departments/{} - Deleting department", id);

        departmentService.deleteDepartment(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<DepartmentDTO> toModel(DepartmentDTO department) {
        return EntityModel.of(department,
                linkTo(methodOn(DepartmentController.class).getDepartmentById(department.getDeptNo())).withSelfRel(),
                linkTo(methodOn(DepartmentController.class).getAllDepartments()).withRel("departments"),
                linkTo(methodOn(DepartmentController.class).updateDepartment(department.getDeptNo(), department)).withRel("update"),
                linkTo(methodOn(DepartmentController.class).deleteDepartment(department.getDeptNo())).withRel("delete"));

    }
}
