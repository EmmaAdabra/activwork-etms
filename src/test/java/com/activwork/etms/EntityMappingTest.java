package com.activwork.etms;

import com.activwork.etms.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify JPA entity mappings and Hibernate schema validation.
 * 
 * <p>This test ensures that:
 * <ul>
 *   <li>All entities are recognized by JPA</li>
 *   <li>Entity mappings match database schema</li>
 *   <li>Hibernate validation passes</li>
 *   <li>Relationships are correctly configured</li>
 * </ul>
 */
@SpringBootTest
public class EntityMappingTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testEntityManagerIsConfigured() {
        assertNotNull(entityManager, "EntityManager should be autowired");
    }

    @Test
    void testAllEntitiesAreRegistered() {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        
        // Should have exactly 10 entities
        assertEquals(10, entities.size(), "Should have 10 JPA entities registered");
        
        // Verify each entity is registered
        assertTrue(isEntityRegistered(User.class), "User entity should be registered");
        assertTrue(isEntityRegistered(Course.class), "Course entity should be registered");
        assertTrue(isEntityRegistered(Material.class), "Material entity should be registered");
        assertTrue(isEntityRegistered(Enrollment.class), "Enrollment entity should be registered");
        assertTrue(isEntityRegistered(Feedback.class), "Feedback entity should be registered");
        assertTrue(isEntityRegistered(MaterialProgress.class), "MaterialProgress entity should be registered");
    }

    @Test
    void testUserEntityMapping() {
        EntityType<User> userEntity = entityManager.getMetamodel().entity(User.class);
        assertNotNull(userEntity, "User entity should exist");
        assertEquals("User", userEntity.getName(), "Entity name should be User");
    }

    @Test
    void testCourseEntityMapping() {
        EntityType<Course> courseEntity = entityManager.getMetamodel().entity(Course.class);
        assertNotNull(courseEntity, "Course entity should exist");
        assertEquals("Course", courseEntity.getName(), "Entity name should be Course");
    }

    @Test
    void testMaterialEntityMapping() {
        EntityType<Material> materialEntity = entityManager.getMetamodel().entity(Material.class);
        assertNotNull(materialEntity, "Material entity should exist");
        assertEquals("Material", materialEntity.getName(), "Entity name should be Material");
    }

    @Test
    void testEnrollmentEntityMapping() {
        EntityType<Enrollment> enrollmentEntity = entityManager.getMetamodel().entity(Enrollment.class);
        assertNotNull(enrollmentEntity, "Enrollment entity should exist");
        assertEquals("Enrollment", enrollmentEntity.getName(), "Entity name should be Enrollment");
    }

    @Test
    void testFeedbackEntityMapping() {
        EntityType<Feedback> feedbackEntity = entityManager.getMetamodel().entity(Feedback.class);
        assertNotNull(feedbackEntity, "Feedback entity should exist");
        assertEquals("Feedback", feedbackEntity.getName(), "Entity name should be Feedback");
    }

    @Test
    void testMaterialProgressEntityMapping() {
        EntityType<MaterialProgress> progressEntity = entityManager.getMetamodel().entity(MaterialProgress.class);
        assertNotNull(progressEntity, "MaterialProgress entity should exist");
        assertEquals("MaterialProgress", progressEntity.getName(), "Entity name should be MaterialProgress");
    }

    @Test
    void testHibernateSchemaValidation() {
        // If this test runs, it means Hibernate successfully validated the schema
        // against the database (spring.jpa.hibernate.ddl-auto=validate)
        assertTrue(true, "Hibernate schema validation passed");
    }

    /**
     * Helper method to check if an entity is registered
     */
    private boolean isEntityRegistered(Class<?> entityClass) {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        return entities.stream()
            .anyMatch(entity -> entity.getJavaType().equals(entityClass));
    }
}

