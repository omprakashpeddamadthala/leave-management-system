package com.hcltech.leave;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Leave Management System REST API Documentation",
                version = "v1.0",
                description = "HCLTech Leave Management System REST API Documentation - " +
                        "A comprehensive employee leave management system with features for " +
                        "applying leaves, approval workflows, and leave balance tracking",
                contact = @Contact(
                        name = "HCLTech Development Team",
                        email = "your.email@hcltech.com",
                        url = "https://www.hcltech.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Leave Management System REST API Documentation",
                url = "https://www.hcltech.com/swagger-ui.html"
        )
)
public class LeaveManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaveManagementSystemApplication.class, args);
    }

}
