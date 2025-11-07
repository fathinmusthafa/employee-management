package com.example.assessment.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.assessment.dto.EmployeeDTO;
import com.example.assessment.exception.ResourceNotFoundException;
import com.example.assessment.model.Gender;
import com.example.assessment.service.EmployeeService;
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

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDTO sampleEmployee;
    private final Integer EMPLOYEE_ID = 1;
    private final String FIRST_NAME = "John";
    private final String LAST_NAME = "Doe";

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        sampleEmployee = EmployeeDTO.builder()
                .empNo(EMPLOYEE_ID)
                .birthDate(LocalDate.of(1990, 1, 1))
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .gender(Gender.M)
                .hireDate(LocalDate.now())
                .build();
    }

    @Test
    void getAllEmployeesSuccess() throws Exception {

        List<EmployeeDTO> employees = Arrays.asList(sampleEmployee);
        given(employeeService.getAllEmployees()).willReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employeeDTOList[0].empNo").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$._embedded.employeeDTOList[0].firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void getEmployeeByIdSuccess() throws Exception {

        given(employeeService.getEmployeeById(EMPLOYEE_ID)).willReturn(sampleEmployee);

        mockMvc.perform(get("/api/employees/{id}", EMPLOYEE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empNo").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(employeeService, times(1)).getEmployeeById(EMPLOYEE_ID);
    }

    @Test
    void getEmployeeByIdErrorNotFound() throws Exception {

        given(employeeService.getEmployeeById(anyInt())).willThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/{id}", 999))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).getEmployeeById(999);
    }

    @Test
    void createEmployeeSuccess() throws Exception {

        given(employeeService.createEmployee(any(EmployeeDTO.class))).willReturn(sampleEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.empNo").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(employeeService, times(1)).createEmployee(any(EmployeeDTO.class));
    }

    @Test
    void createEmployeeErrorValidation() throws Exception {

        EmployeeDTO invalidEmployee = EmployeeDTO.builder()
                .empNo(-1) // Invalid negative number
                .birthDate(LocalDate.of(2999, 1, 1)) // Future date
                .firstName("") // Empty first name
                .build();

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmployee)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).createEmployee(any());
    }

    @Test
    void updateEmployeeSuccess() throws Exception {

        given(employeeService.updateEmployee(eq(EMPLOYEE_ID), any(EmployeeDTO.class))).willReturn(sampleEmployee);

        mockMvc.perform(put("/api/employees/{id}", EMPLOYEE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empNo").value(EMPLOYEE_ID))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(employeeService, times(1)).updateEmployee(eq(EMPLOYEE_ID), any(EmployeeDTO.class));
    }

    @Test
    void deleteEmployeeSuccess() throws Exception {

        willDoNothing().given(employeeService).deleteEmployee(EMPLOYEE_ID);

        mockMvc.perform(delete("/api/employees/{id}", EMPLOYEE_ID))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee(EMPLOYEE_ID);
    }

    @Test
    void searchEmployeesSuccess() throws Exception {

        List<EmployeeDTO> employees = Arrays.asList(sampleEmployee);
        given(employeeService.searchEmployeesByName(anyString())).willReturn(employees);

        mockMvc.perform(get("/api/employees/search")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employeeDTOList[0].firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(employeeService, times(1)).searchEmployeesByName("John");
    }

}
