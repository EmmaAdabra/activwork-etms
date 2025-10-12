package com.activwork.etms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify PostgreSQL database connectivity and schema setup.
 * 
 * <p>This test ensures that:
 * <ul>
 *   <li>Database connection is established</li>
 *   <li>ETMS schema exists</li>
 *   <li>All required tables are present</li>
 *   <li>Sample data is loaded</li>
 * </ul>
 */
@SpringBootTest
public class DatabaseConnectivityTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnection() {
        // Test basic connectivity
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result, "Database connection should work");
    }

    @Test
    void testEtmsSchemaExists() {
        // Verify ETMS schema exists
        Integer schemaCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = 'etms'",
            Integer.class
        );
        assertEquals(1, schemaCount, "ETMS schema should exist");
    }

    @Test
    void testCoreTablesExist() {
        // Verify all core tables exist in etms schema
        String[] coreTables = {"users", "courses", "materials", "enrollments", "feedback", "live_sessions"};
        
        for (String table : coreTables) {
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = 'etms' AND table_name = ?",
                Integer.class,
                table
            );
            assertEquals(1, tableCount, "Table " + table + " should exist in etms schema");
        }
    }

    @Test
    void testInnovativeTablesExist() {
        // Verify innovative tables exist
        String[] innovativeTables = {"course_prerequisites", "material_progress", "notifications", "course_analytics"};
        
        for (String table : innovativeTables) {
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = 'etms' AND table_name = ?",
                Integer.class,
                table
            );
            assertEquals(1, tableCount, "Table " + table + " should exist in etms schema");
        }
    }

    @Test
    void testSampleDataLoaded() {
        // Verify sample data is present
        jdbcTemplate.execute("SET search_path TO etms, public");
        
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM etms.users", Integer.class);
        assertTrue(userCount >= 9, "Should have at least 9 sample users (4 instructors + 5 learners)");
        
        Integer courseCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM etms.courses", Integer.class);
        assertTrue(courseCount >= 6, "Should have at least 6 sample courses");
    }

    @Test
    void testTriggersExist() {
        // Verify triggers are installed
        Integer triggerCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.triggers " +
            "WHERE trigger_schema = 'etms' AND trigger_name LIKE 'trigger_%'",
            Integer.class
        );
        assertTrue(triggerCount >= 3, "Should have at least 3 triggers installed");
    }
}

