package com.example.assessment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import com.example.assessment.dto.SalaryDTO;
import com.example.assessment.exception.ResourceNotFoundException;
import com.example.assessment.service.SalaryService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(SalaryController.class)
public class SalaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalaryService salaryService;

    @Autowired
    private ObjectMapper objectMapper;

    private SalaryDTO sampleSalary;
    private final Integer EMPLOYEE_ID = 1;
    private final String FROM_DATE = "2023-01-01";
    private final String TO_DATE = "2023-12-31";
    private final Integer SALARY_AMOUNT = 5000000;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        sampleSalary = SalaryDTO.builder()
                .empNo(EMPLOYEE_ID)
                .fromDate(LocalDate.parse(FROM_DATE))
                .toDate(LocalDate.parse(TO_DATE))
                .salary(SALARY_AMOUNT)
                .build();
    }

    @Test
    void getAllSalariesSuccess() throws Exception {

        List<SalaryDTO> salaries = Arrays.asList(sampleSalary);
        given(salaryService.getAllSalaries()).willReturn(salaries);

        mockMvc.perform(get("/api/salaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.salaryDTOList[0].empNo").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$._embedded.salaryDTOList[0].salary").value(SALARY_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(salaryService, times(1)).getAllSalaries();
    }

    @Test
    void getSalariesByEmployeeSuccess() throws Exception {
        List<SalaryDTO> salaries = Arrays.asList(sampleSalary);
        given(salaryService.getSalariesByEmployee(EMPLOYEE_ID)).willReturn(salaries);

        mockMvc.perform(get("/api/salaries/employee/{empNo}", EMPLOYEE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.salaryDTOList[0].empNo").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$._embedded.salaryDTOList[0].salary").value(SALARY_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.all-salaries.href").exists())
                .andExpect(jsonPath("$._links.employee.href").exists());

        verify(salaryService, times(1)).getSalariesByEmployee(EMPLOYEE_ID);
    }

    @Test
    void getSalariesByEmployeeErrorNotFound() throws Exception {
        given(salaryService.getSalariesByEmployee(anyInt())).willThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/salaries/employee/{empNo}", 999))
                .andExpect(status().isNotFound());

        verify(salaryService, times(1)).getSalariesByEmployee(999);
    }

    @Test
    void createSalarySuccess() throws Exception {
        given(salaryService.createSalary(any(SalaryDTO.class))).willReturn(sampleSalary);

        mockMvc.perform(post("/api/salaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleSalary)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.empNo").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$.salary").value(SALARY_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$._links.salary-history.href").exists())
                .andExpect(jsonPath("$._links.all-salaries.href").exists())
                .andExpect(jsonPath("$._links.employee.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());

        verify(salaryService, times(1)).createSalary(any(SalaryDTO.class));
    }

    @Test
    void createSalaryErrorValidation() throws Exception {

        SalaryDTO invalidSalary = SalaryDTO.builder()
                .empNo(-1) // Invalid negative employee number
                .salary(0) // Invalid zero salary
                .build();

        mockMvc.perform(post("/api/salaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSalary)))
                .andExpect(status().isBadRequest());

        verify(salaryService, never()).createSalary(any());
    }

    @Test
    void updateSalarySuccess() throws Exception {
        given(salaryService.updateSalary(eq(EMPLOYEE_ID), eq(FROM_DATE), any(SalaryDTO.class)))
                .willReturn(sampleSalary);

        mockMvc.perform(put("/api/salaries/{empNo}/{fromDate}", EMPLOYEE_ID, FROM_DATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleSalary)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empNo").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$.salary").value(SALARY_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$._links.salary-history.href").exists())
                .andExpect(jsonPath("$._links.all-salaries.href").exists())
                .andExpect(jsonPath("$._links.employee.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());

        verify(salaryService, times(1)).updateSalary(eq(EMPLOYEE_ID), eq(FROM_DATE), any(SalaryDTO.class));
    }

    @Test
    void updateSalaryErrorNotFound() throws Exception {
        given(salaryService.updateSalary(eq(EMPLOYEE_ID), eq(FROM_DATE), any(SalaryDTO.class)))
                .willThrow(new ResourceNotFoundException("Salary not found"));

        mockMvc.perform(put("/api/salaries/{empNo}/{fromDate}", EMPLOYEE_ID, FROM_DATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleSalary)))
                .andExpect(status().isNotFound());

        verify(salaryService, times(1)).updateSalary(eq(EMPLOYEE_ID), eq(FROM_DATE), any(SalaryDTO.class));
    }

    @Test
    void deleteSalarySuccess() throws Exception {
        willDoNothing().given(salaryService).deleteSalary(EMPLOYEE_ID, FROM_DATE);

        mockMvc.perform(delete("/api/salaries/{empNo}/{fromDate}", EMPLOYEE_ID, FROM_DATE))
                .andExpect(status().isNoContent());

        verify(salaryService, times(1)).deleteSalary(EMPLOYEE_ID, FROM_DATE);
    }

    @Test
    void deleteSalaryErrorNotFound() throws Exception {
        willThrow(new ResourceNotFoundException("Salary not found"))
                .given(salaryService).deleteSalary(EMPLOYEE_ID, FROM_DATE);

        // Act & Assert
        mockMvc.perform(delete("/api/salaries/{empNo}/{fromDate}", EMPLOYEE_ID, FROM_DATE))
                .andExpect(status().isNotFound());

        verify(salaryService, times(1)).deleteSalary(EMPLOYEE_ID, FROM_DATE);
    }

}
