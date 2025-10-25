package com.example.mealplanner.service;

import static com.example.mealplanner.exception.MealPlannerExceptions.*;

import com.example.mealplanner.model.Food;
import com.example.mealplanner.model.User;
import com.example.mealplanner.repository.FoodRepository;
import com.example.mealplanner.repository.MealRepository;
import com.example.mealplanner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Lambda Methods Tests")
class LambdaMethodsTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private MealRepository mealRepository;
    
    @Mock
    private FoodRepository foodRepository;

    @Mock
    private NutritionService nutritionService;

    @InjectMocks
    private MealPlannerService mealPlannerService;
    
    private User testUser;
    private Food testFood1;
    private Food testFood2;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        
        testFood1 = new Food();
        testFood1.setId(1L);
        testFood1.setName("Test Food 1");
        
        testFood2 = new Food();
        testFood2.setId(2L);
        testFood2.setName("Test Food 2");
        
        testDate = LocalDate.now();
    }

    @Test
    @DisplayName("Should track water intake successfully")
    void should_TrackWaterIntake_Successfully() {
        // Given
        Long userId = 1L;
        double amount = 250.0;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        mealPlannerService.trackWaterIntake(userId, amount, testDate);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        Map<LocalDate, Double> waterIntake = savedUser.getWaterIntake();
        assertEquals(250.0, waterIntake.get(testDate));
    }

    @Test
    @DisplayName("Should accumulate water intake for same date")
    void should_AccumulateWaterIntake_ForSameDate() {
        // Given
        Long userId = 1L;
        double firstAmount = 250.0;
        double secondAmount = 300.0;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        mealPlannerService.trackWaterIntake(userId, firstAmount, testDate);
        mealPlannerService.trackWaterIntake(userId, secondAmount, testDate);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(userCaptor.capture());
        
        User savedUser = userCaptor.getAllValues().get(1);
        Map<LocalDate, Double> waterIntake = savedUser.getWaterIntake();
        assertEquals(550.0, waterIntake.get(testDate));
    }

    @Test
    @DisplayName("Should throw exception for invalid water intake amount")
    void should_ThrowException_ForInvalidWaterIntakeAmount() {
        // Given
        Long userId = 1L;
        double invalidAmount = -100.0;

        // When & Then
        assertThrows(InvalidInputException.class, () -> 
            mealPlannerService.trackWaterIntake(userId, invalidAmount, testDate)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should suggest meals based on preferences")
    void should_SuggestMeals_BasedOnPreferences() {
        // Given
        Long userId = 1L;
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("diet", "vegetarian");
        preferences.put("maxCalories", 500);
        List<Food> expectedFoods = Arrays.asList(testFood1, testFood2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(nutritionService.findSuitableMeals(eq(testUser), eq(preferences)))
            .thenReturn(expectedFoods);

        // When
        List<Food> result = mealPlannerService.suggestMeals(userId, preferences);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testFood1, result.get(0));
        assertEquals(testFood2, result.get(1));
        verify(nutritionService).findSuitableMeals(testUser, preferences);
    }

    @Test
    @DisplayName("Should throw exception for empty meal preferences")
    void should_ThrowException_ForEmptyMealPreferences() {
        // Given
        Long userId = 1L;
        Map<String, Object> preferences = new HashMap<>();

        // When & Then
        assertThrows(InvalidInputException.class, () ->
            mealPlannerService.suggestMeals(userId, preferences)
        );
        verify(nutritionService, never()).findSuitableMeals(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void should_ThrowException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () ->
            mealPlannerService.suggestMeals(userId, Map.of("diet", "vegan"))
        );
        verify(nutritionService, never()).findSuitableMeals(any(), any());
    }
}