package com.example.mealplanner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MealplannerApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should not be null");
        assertTrue(applicationContext.getBeanDefinitionCount() > 0, "Should have beans defined");
    }

    @Test
    void mainMethodShouldRun() {
        MealplannerApplication.main(new String[] {});
        assertTrue(true, "Main method should run without throwing exceptions");
    }
}
