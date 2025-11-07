package com.example.assessment.controller;

import com.example.assessment.dto.TitleDTO;
import com.example.assessment.service.TitleService;
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
@RequestMapping("/api/titles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Title Management", description = "APIs for managing employee titles")
public class TitleController {

    private final TitleService titleService;

    @GetMapping
    @Operation(summary = "Get all titles", description = "Retrieve a list of all employee titles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CollectionModel<EntityModel<TitleDTO>>> getAllTitles() {
        log.info("GET /api/titles - Fetching all titles");

        List<EntityModel<TitleDTO>> titles = titleService.getAllTitles().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<TitleDTO>> collectionModel = CollectionModel.of(titles,
                linkTo(methodOn(TitleController.class).getAllTitles()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/employee/{empNo}")
    @Operation(summary = "Get titles by employee", description = "Retrieve title history for a specific employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved titles"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<CollectionModel<EntityModel<TitleDTO>>> getTitlesByEmployee(
            @PathVariable Integer empNo) {
        log.info("GET /api/titles/employee/{} - Fetching titles", empNo);

        List<EntityModel<TitleDTO>> titles = titleService.getTitlesByEmployee(empNo).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<TitleDTO>> collectionModel = CollectionModel.of(titles,
                linkTo(methodOn(TitleController.class).getTitlesByEmployee(empNo)).withSelfRel(),
                linkTo(methodOn(TitleController.class).getAllTitles()).withRel("all-titles"));

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/employee/{empNo}/current")
    @Operation(summary = "Get current title", description = "Get the current title for an employee")
    public ResponseEntity<EntityModel<TitleDTO>> getCurrentTitle(@PathVariable Integer empNo) {
        log.info("GET /api/titles/employee/{}/current - Fetching current title", empNo);

        TitleDTO title = titleService.getCurrentTitle(empNo);
        EntityModel<TitleDTO> model = toModel(title);

        return ResponseEntity.ok(model);
    }


    @PostMapping
    @Operation(summary = "Create new title", description = "Assign a new title to an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Title created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<EntityModel<TitleDTO>> createTitle(@Valid @RequestBody TitleDTO titleDTO) {
        log.info("POST /api/titles - Creating new title");

        TitleDTO created = titleService.createTitle(titleDTO);
        EntityModel<TitleDTO> model = toModel(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{empNo}/{fromDate}")
    @Operation(summary = "Update title", description = "Update an existing title")
    public ResponseEntity<EntityModel<TitleDTO>> updateTitle(
            @PathVariable Integer empNo,
            @PathVariable String fromDate,
            @Valid @RequestBody TitleDTO titleDTO) {
        log.info("PUT /api/titles/{}/{} - Updating title", empNo, fromDate);

        TitleDTO updated = titleService.updateTitle(empNo, fromDate, titleDTO);
        EntityModel<TitleDTO> model = toModel(updated);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{empNo}/{fromDate}")
    @Operation(summary = "Delete title", description = "Delete a title record")
    public ResponseEntity<Void> deleteTitle(
            @PathVariable Integer empNo,
            @PathVariable String fromDate) {
        log.info("DELETE /api/titles/{}/{} - Deleting title", empNo, fromDate);

        titleService.deleteTitle(empNo, fromDate);

        return ResponseEntity.noContent().build();
    }

    private EntityModel<TitleDTO> toModel(TitleDTO title) {
        return EntityModel.of(title,
                linkTo(methodOn(TitleController.class)
                        .getTitlesByEmployee(title.getEmpNo())).withSelfRel(),
                linkTo(methodOn(TitleController.class)
                        .getAllTitles()).withRel("all-titles"),
                linkTo(methodOn(EmployeeController.class)
                        .getEmployeeById(title.getEmpNo())).withRel("employee"));
    }
}
