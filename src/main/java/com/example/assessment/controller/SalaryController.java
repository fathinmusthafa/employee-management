package com.example.assessment.controller;

import com.example.assessment.dto.SalaryDTO;
import com.example.assessment.service.SalaryService;
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
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Salary Management", description = "APIs for managing salaries")
public class SalaryController {

    private final SalaryService salaryService;

    @GetMapping
    @Operation(summary = "Get all salaries", description = "Retrieve a list of all salary records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CollectionModel<EntityModel<SalaryDTO>>> getAllSalaries() {
        log.info("GET /api/salaries - Fetching all salaries");

        List<EntityModel<SalaryDTO>> salaries = salaryService.getAllSalaries().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<SalaryDTO>> collectionModel = CollectionModel.of(salaries,
                linkTo(methodOn(SalaryController.class).getAllSalaries()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/employee/{empNo}")
    @Operation(summary = "Get salaries by employee", description = "Retrieve salary history for a specific employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved salaries"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<CollectionModel<EntityModel<SalaryDTO>>> getSalariesByEmployee(
            @PathVariable Integer empNo) {
        log.info("GET /api/salaries/employee/{} - Fetching salaries", empNo);

        List<EntityModel<SalaryDTO>> salaries = salaryService.getSalariesByEmployee(empNo).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<SalaryDTO>> collectionModel = CollectionModel.of(salaries,
                linkTo(methodOn(SalaryController.class).getSalariesByEmployee(empNo)).withSelfRel(),
                linkTo(methodOn(SalaryController.class).getAllSalaries()).withRel("all-salaries"),
                linkTo(methodOn(EmployeeController.class).getEmployeeById(empNo)).withRel("employee"));

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping
    @Operation(summary = "Create new salary", description = "Create a new salary record for an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Salary created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<EntityModel<SalaryDTO>> createSalary(@Valid @RequestBody SalaryDTO salaryDTO) {
        log.info("POST /api/salaries - Creating new salary");

        SalaryDTO created = salaryService.createSalary(salaryDTO);
        EntityModel<SalaryDTO> model = toModel(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{empNo}/{fromDate}")
    @Operation(summary = "Update salary", description = "Update an existing salary record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salary updated successfully"),
            @ApiResponse(responseCode = "404", description = "Salary not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<EntityModel<SalaryDTO>> updateSalary(
            @PathVariable Integer empNo,
            @PathVariable String fromDate,
            @Valid @RequestBody SalaryDTO salaryDTO) {
        log.info("PUT /api/salaries/{}/{} - Updating salary", empNo, fromDate);

        SalaryDTO updated = salaryService.updateSalary(empNo, fromDate, salaryDTO);
        EntityModel<SalaryDTO> model = toModel(updated);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{empNo}/{fromDate}")
    @Operation(summary = "Delete salary", description = "Delete a salary record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Salary deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Salary not found")
    })
    public ResponseEntity<Void> deleteSalary(
            @PathVariable Integer empNo,
            @PathVariable String fromDate) {
        log.info("DELETE /api/salaries/{}/{} - Deleting salary", empNo, fromDate);

        salaryService.deleteSalary(empNo, fromDate);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<SalaryDTO> toModel(SalaryDTO salary) {
        return EntityModel.of(salary,
                linkTo(methodOn(SalaryController.class)
                        .getSalariesByEmployee(salary.getEmpNo())).withRel("salary-history"),
                linkTo(methodOn(SalaryController.class)
                        .getAllSalaries()).withRel("all-salaries"),
                linkTo(methodOn(EmployeeController.class)
                        .getEmployeeById(salary.getEmpNo())).withRel("employee"),
                linkTo(methodOn(SalaryController.class)
                        .updateSalary(salary.getEmpNo(), salary.getFromDate().toString(), salary))
                        .withRel("update"),
                linkTo(methodOn(SalaryController.class)
                        .deleteSalary(salary.getEmpNo(), salary.getFromDate().toString()))
                        .withRel("delete")
        );
    }
}
