package com.example.mealplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Food Model Tests")
class FoodTest {
    
    private Food food;

    @BeforeEach
    void setUp() {
        food = new Food();
    }

    // 1. Basic Properties Tests
    @Test
    @DisplayName("Should create food with all properties")
    void should_CreateFood_WithAllProperties() {
        // Given & When
        food.setId(1L);
        food.setName("Chicken Breast");
        food.setCalories(165);
        food.setProtein(31);
        food.setCarbs(0);
        food.setFats(3.6);
        food.setIngredients(Arrays.asList("Chicken"));

        // Then
        assertEquals(1L, food.getId());
        assertEquals("Chicken Breast", food.getName());
        assertEquals(165, food.getCalories());
        assertEquals(31, food.getProtein());
        assertEquals(0, food.getCarbs());
        assertEquals(3.6, food.getFats(), 0.01);
        assertEquals(1, food.getIngredients().size());
        assertTrue(food.getIngredients().contains("Chicken"));
    }

    // 2. Calories Validation Tests
    @Test
    @DisplayName("Should validate negative calories")
    void should_ValidateNegative_Calories() {
        assertThrows(IllegalArgumentException.class, () -> food.setCalories(-100));
    }

    @Test
    @DisplayName("Should accept zero calories")
    void should_Accept_ZeroCalories() {
        assertDoesNotThrow(() -> food.setCalories(0));
        assertEquals(0, food.getCalories());
    }

    @Test
    @DisplayName("Should accept valid calories")
    void should_Accept_ValidCalories() {
        assertDoesNotThrow(() -> food.setCalories(500));
        assertEquals(500, food.getCalories());
    }

    // 3. Protein Validation Tests
    @Test
    @DisplayName("Should validate negative protein")
    void should_ValidateNegative_Protein() {
        assertThrows(IllegalArgumentException.class, () -> food.setProtein(-50));
    }

    @Test
    @DisplayName("Should accept zero protein")
    void should_Accept_ZeroProtein() {
        assertDoesNotThrow(() -> food.setProtein(0));
        assertEquals(0, food.getProtein());
    }

    @Test
    @DisplayName("Should accept valid protein")
    void should_Accept_ValidProtein() {
        assertDoesNotThrow(() -> food.setProtein(30));
        assertEquals(30, food.getProtein());
    }

    // 4. Carbs Validation Tests
    @Test
    @DisplayName("Should validate negative carbs")
    void should_ValidateNegative_Carbs() {
        assertThrows(IllegalArgumentException.class, () -> food.setCarbs(-30));
    }

    @Test
    @DisplayName("Should accept zero carbs")
    void should_Accept_ZeroCarbs() {
        assertDoesNotThrow(() -> food.setCarbs(0));
        assertEquals(0, food.getCarbs());
    }

    @Test
    @DisplayName("Should accept valid carbs")
    void should_Accept_ValidCarbs() {
        assertDoesNotThrow(() -> food.setCarbs(50));
        assertEquals(50, food.getCarbs());
    }

    // 5. Fats Validation Tests
    @Test
    @DisplayName("Should validate negative fats")
    void should_ValidateNegative_Fats() {
        assertThrows(IllegalArgumentException.class, () -> food.setFats(-10));
    }

    @Test
    @DisplayName("Should accept zero fats")
    void should_Accept_ZeroFats() {
        assertDoesNotThrow(() -> food.setFats(0));
        assertEquals(0, food.getFats());
    }

    @Test
    @DisplayName("Should accept valid fats")
    void should_Accept_ValidFats() {
        assertDoesNotThrow(() -> food.setFats(20));
        assertEquals(20, food.getFats());
    }

    // 6. Ingredients Management Tests
    @Test
    @DisplayName("Should handle empty ingredients list")
    void should_HandleEmpty_IngredientsList() {
        assertNotNull(food.getIngredients());
        assertTrue(food.getIngredients().isEmpty());
    }

    @Test
    @DisplayName("Should add ingredients correctly")
    void should_AddIngredients_Correctly() {
        // Given
        List<String> ingredients = Arrays.asList("Rice", "Salt", "Pepper");
        
        // When
        food.setIngredients(ingredients);
        
        // Then
        assertEquals(3, food.getIngredients().size());
        assertTrue(food.getIngredients().containsAll(ingredients));
    }

    @Test
    @DisplayName("Should update ingredients correctly")
    void should_UpdateIngredients_Correctly() {
        // Given
        food.setIngredients(Arrays.asList("Rice"));
        
        // When
        List<String> newIngredients = Arrays.asList("Rice", "Salt");
        food.setIngredients(newIngredients);
        
        // Then
        assertEquals(2, food.getIngredients().size());
        assertTrue(food.getIngredients().containsAll(newIngredients));
    }

    // 7. Object Equality Tests
    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void should_ImplementEqualsAndHashCode_Correctly() {
        // Given
        Food food1 = new Food();
        food1.setId(1L);
        food1.setName("Rice");

        Food food2 = new Food();
        food2.setId(1L);
        food2.setName("Rice");

        Food food3 = new Food();
        food3.setId(2L);
        food3.setName("Rice");

        // Then
        assertEquals(food1, food2);
        assertNotEquals(food1, food3);
        assertEquals(food1.hashCode(), food2.hashCode());
    }

    // 8. ToString Test
    @Test
    @DisplayName("Should implement toString correctly")
    void should_ImplementToString_Correctly() {
        // Given
        food.setName("Rice");
        food.setCalories(130);
        
        // When
        String result = food.toString();
        
        // Then
        assertTrue(result.contains("Rice"));
        assertTrue(result.contains("130"));
    }

    // 9. Null Handling Tests
    @Test
    @DisplayName("Should handle null name gracefully")
    void should_HandleNullName_Gracefully() {
        assertDoesNotThrow(() -> food.setName(null));
    }

    // 10. Constructor Test
    @Test
    @DisplayName("Should create food with no-args constructor")
    void should_CreateFood_WithNoArgsConstructor() {
        assertNotNull(food);
        assertNotNull(food.getIngredients());
        assertEquals(0, food.getCalories());
        assertEquals(0, food.getProtein());
        assertEquals(0, food.getCarbs());
        assertEquals(0, food.getFats());
    }


}