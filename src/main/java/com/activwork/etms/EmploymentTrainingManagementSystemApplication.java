package com.activwork.etms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main Application Class for Employment Training Management System (ETMS)
 * 
 * <p>This application demonstrates:
 * <ul>
 *   <li>Layered Architecture (Presentation → Service → Persistence)</li>
 *   <li>Model-View-Controller (MVC) pattern</li>
 *   <li>GRASP patterns (Information Expert, Creator, Controller)</li>
 * </ul>
 * 
 * @author ETMS Development Team
 * @version 1.0
 * @since 2025-10-12
 */
@SpringBootApplication
@EnableConfigurationProperties
public class EmploymentTrainingManagementSystemApplication {

    /**
     * Main entry point for the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EmploymentTrainingManagementSystemApplication.class, args);
        
        System.out.println("\n" +
                "═══════════════════════════════════════════════════════════════\n" +
                "  Employment Training Management System (ETMS) Started!\n" +
                "═══════════════════════════════════════════════════════════════\n" +
                "  🌐 Application: http://localhost:8080\n" +
                "  📊 Database: PostgreSQL (etms schema)\n" +
                "  🔧 Profile: Development\n" +
                "  📚 Documentation: See project_requirement.md\n" +
                "═══════════════════════════════════════════════════════════════\n");
    }
}
