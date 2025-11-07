package com.example.assessment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.assessment.dto.DepartmentDTO;
import com.example.assessment.exception.ResourceNotFoundException;
import com.example.assessment.service.DepartmentService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentDTO sampleDepartment;
    private final String DEPARTMENT_ID = "D001";
    private final String DEPARTMENT_NAME = "Engineering";

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        sampleDepartment = DepartmentDTO.builder()
                .deptNo(DEPARTMENT_ID)
                .deptName(DEPARTMENT_NAME)
                .build();
    }

    @Test
    void getAllDepartmentsSuccess() throws Exception {

        List<DepartmentDTO> departments = Arrays.asList(sampleDepartment);
        given(departmentService.getAllDepartments()).willReturn(departments);

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.departmentDTOList[0].deptNo").value(DEPARTMENT_ID))
                .andExpect(jsonPath("$._embedded.departmentDTOList[0].deptName").value(DEPARTMENT_NAME))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    void getDepartmentByIdSuccess() throws Exception {

        given(departmentService.getDepartmentById(DEPARTMENT_ID)).willReturn(sampleDepartment);

        mockMvc.perform(get("/api/departments/{id}", DEPARTMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deptNo").value(DEPARTMENT_ID))
                .andExpect(jsonPath("$.deptName").value(DEPARTMENT_NAME))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.departments.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());

        verify(departmentService, times(1)).getDepartmentById(DEPARTMENT_ID);
    }

    @Test
    void getDepartmentByIdErrorNotFound() throws Exception {

        given(departmentService.getDepartmentById(anyString())).willThrow(new ResourceNotFoundException("Department not found"));

        mockMvc.perform(get("/api/departments/{id}", "INVALID_ID"))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).getDepartmentById("INVALID_ID");
    }

    @Test
    void createDepartmenSuccess() throws Exception {

        given(departmentService.createDepartment(any(DepartmentDTO.class))).willReturn(sampleDepartment);

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDepartment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deptNo").value(DEPARTMENT_ID))
                .andExpect(jsonPath("$.deptName").value(DEPARTMENT_NAME))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.departments.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());

        verify(departmentService, times(1)).createDepartment(any(DepartmentDTO.class));
    }

    @Test
    void createDepartmentErrorValidation() throws Exception {

        DepartmentDTO invalidDepartment = DepartmentDTO.builder()
                .deptNo("") // Empty department number
                .deptName("") // Empty department name
                .build();

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDepartment)))
                .andExpect(status().isBadRequest());

        verify(departmentService, never()).createDepartment(any());
    }

    @Test
    void updateDepartmentSuccess() throws Exception {

        given(departmentService.updateDepartment(eq(DEPARTMENT_ID), any(DepartmentDTO.class))).willReturn(sampleDepartment);

        mockMvc.perform(put("/api/departments/{id}", DEPARTMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDepartment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deptNo").value(DEPARTMENT_ID))
                .andExpect(jsonPath("$.deptName").value(DEPARTMENT_NAME))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.departments.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());

        verify(departmentService, times(1)).updateDepartment(eq(DEPARTMENT_ID), any(DepartmentDTO.class));
    }

    @Test
    void deleteDepartmentSuccess() throws Exception {

        willDoNothing().given(departmentService).deleteDepartment(DEPARTMENT_ID);

        mockMvc.perform(delete("/api/departments/{id}", DEPARTMENT_ID))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(DEPARTMENT_ID);
    }


    @Test
    void updateDepartmentErrorNotFound() throws Exception {
        // Arrange
        given(departmentService.updateDepartment(eq("INVALID_ID"), any(DepartmentDTO.class)))
                .willThrow(new ResourceNotFoundException("Department not found"));

        // Act & Assert
        mockMvc.perform(put("/api/departments/{id}", "INVALID_ID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDepartment)))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).updateDepartment(eq("INVALID_ID"), any(DepartmentDTO.class));
    }

    @Test
    void deleteDepartmentErrorNotFound() throws Exception {
        // Arrange
        willThrow(new ResourceNotFoundException("Department not found"))
                .given(departmentService).deleteDepartment("INVALID_ID");

        // Act & Assert
        mockMvc.perform(delete("/api/departments/{id}", "INVALID_ID"))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).deleteDepartment("INVALID_ID");
    }
}
