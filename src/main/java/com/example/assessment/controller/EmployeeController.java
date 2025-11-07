package com.example.assessment.controller;

import com.example.assessment.dto.ApiResponseDTO;
import com.example.assessment.dto.EmployeeDTO;
import com.example.assessment.service.EmployeeService;
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
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieve a list of all employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CollectionModel<EntityModel<EmployeeDTO>>> getAllEmployees() {
        log.info("GET /api/employees - Fetching all employees");

        List<EntityModel<EmployeeDTO>> employees = employeeService.getAllEmployees().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<EmployeeDTO>> collectionModel = CollectionModel.of(employees,
                linkTo(methodOn(EmployeeController.class).getAllEmployees()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Retrieve a specific employee by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employee"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<EntityModel<EmployeeDTO>> getEmployeeById(@PathVariable Integer id) {
        log.info("GET /api/employees/{} - Fetching employee", id);

        EmployeeDTO employee = employeeService.getEmployeeById(id);
        EntityModel<EmployeeDTO> model = toModel(employee);

        return ResponseEntity.ok(model);
    }

    @PostMapping
    @Operation(summary = "Create new employee", description = "Create a new employee record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<EntityModel<EmployeeDTO>> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("POST /api/employees - Creating new employee");

        EmployeeDTO created = employeeService.createEmployee(employeeDTO);
        EntityModel<EmployeeDTO> model = toModel(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee", description = "Update an existing employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<EntityModel<EmployeeDTO>> updateEmployee(
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("PUT /api/employees/{} - Updating employee", id);

        EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
        EntityModel<EmployeeDTO> model = toModel(updated);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee", description = "Delete an employee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        log.info("DELETE /api/employees/{} - Deleting employee", id);

        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees", description = "Search employees by name")
    public ResponseEntity<CollectionModel<EntityModel<EmployeeDTO>>> searchEmployees(
            @RequestParam String name) {
        log.info("GET /api/employees/search?name={}", name);

        List<EntityModel<EmployeeDTO>> employees = employeeService.searchEmployeesByName(name).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<EmployeeDTO>> collectionModel = CollectionModel.of(employees,
                linkTo(methodOn(EmployeeController.class).searchEmployees(name)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    // Stored Procedure Endpoints
    @PostMapping("/procedure")
    @Operation(summary = "Create employee via stored procedure", description = "Create employee using PL/SQL procedure")
    public ResponseEntity<ApiResponseDTO<String>> createEmployeeViaProcedure(@Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("POST /api/employees/procedure - Creating employee via stored procedure");

        employeeService.createEmployeeViaProcedure(employeeDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Employee created via stored procedure",
                        "Employee ID: " + employeeDTO.getEmpNo()));
    }

    @PutMapping("/{id}/procedure")
    @Operation(summary = "Update employee via stored procedure", description = "Update employee using PL/SQL procedure")
    public ResponseEntity<ApiResponseDTO<String>> updateEmployeeViaProcedure(
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("PUT /api/employees/{}/procedure - Updating employee via stored procedure", id);

        employeeService.updateEmployeeViaProcedure(id, employeeDTO);

        return ResponseEntity.ok(ApiResponseDTO.success("Employee updated via stored procedure",
                "Employee ID: " + id));
    }

    @DeleteMapping("/{id}/procedure")
    @Operation(summary = "Delete employee via stored procedure", description = "Delete employee using PL/SQL procedure")
    public ResponseEntity<Void> deleteEmployeeViaProcedure(@PathVariable Integer id) {
        log.info("DELETE /api/employees/{}/procedure - Deleting employee via stored procedure", id);

        employeeService.deleteEmployeeViaProcedure(id);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<EmployeeDTO> toModel(EmployeeDTO employee) {
        return EntityModel.of(employee,
                linkTo(methodOn(EmployeeController.class).getEmployeeById(employee.getEmpNo())).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).getAllEmployees()).withRel("employees"),
                linkTo(methodOn(EmployeeController.class).updateEmployee(employee.getEmpNo(), employee)).withRel("update"),
                linkTo(methodOn(EmployeeController.class).deleteEmployee(employee.getEmpNo())).withRel("delete"));
    }
}
