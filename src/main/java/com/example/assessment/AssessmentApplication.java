package com.example.assessment;

import com.example.assessment.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AssessmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssessmentApplication.class, args);

        System.out.println("\n" +
                "========================================\n" +
                "  Employee Management System Started  \n" +
                "========================================\n" +
                "  Application: http://localhost:8080/\n" +
                "  Swagger UI:  http://localhost:8080/swagger-ui.html\n" +
                "  API Docs:    http://localhost:8080/api/\n" +
                "========================================\n"
        );
	}



}
