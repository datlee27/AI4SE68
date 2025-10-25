package com.example.mealplanner.service;

import com.example.mealplanner.dto.NutritionSummary;
import com.example.mealplanner.model.*;
import com.example.mealplanner.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static com.example.mealplanner.exception.MealPlannerExceptions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Meal Planner Service Tests")
class MealPlannerServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private NutritionService nutritionService;

    @InjectMocks
    private MealPlannerService mealPlannerService;

    private User testUser;
    private Food testFood1;
    private Food testFood2;
    private List<Food> testFoods;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@test.com");

        // Setup test foods
        testFood1 = new Food();
        testFood1.setId(1L);
        testFood1.setName("Chicken Breast");
        testFood1.setCalories(165);
        testFood1.setProtein(31);
        testFood1.getIngredients().add("Chicken");

        testFood2 = new Food();
        testFood2.setId(2L);
        testFood2.setName("Brown Rice");
        testFood2.setCalories(215);
        testFood2.setCarbs(45);
        testFood2.getIngredients().add("Rice");

        testFoods = Arrays.asList(testFood1, testFood2);
        testDate = LocalDate.now();
    }

    @Test
    @DisplayName("Should add meal successfully with valid inputs")
    void should_AddMeal_When_ValidInputs() {
        // Given
        Long userId = 1L;
        MealType mealType = MealType.BREAKFAST;
        List<Long> foodIds = Arrays.asList(1L, 2L);
        
        Meal expectedMeal = new Meal();
        expectedMeal.setId(1L);
        expectedMeal.setUser(testUser);
        expectedMeal.setDate(testDate);
        expectedMeal.setMealType(mealType);
        expectedMeal.setFoods(testFoods);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood1));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(testFood2));
        when(mealRepository.save(any(Meal.class))).thenReturn(expectedMeal);

        // When
        Meal result = mealPlannerService.addMeal(userId, testDate, mealType, foodIds);

        // Then
        assertNotNull(result);
        assertEquals(expectedMeal.getId(), result.getId());
        assertEquals(MealType.BREAKFAST, result.getMealType());
        assertEquals(2, result.getFoods().size());
        
        verify(userRepository).findById(userId);
        verify(foodRepository).findById(1L);
        verify(foodRepository).findById(2L);
        verify(mealRepository).save(any(Meal.class));
    }

    @ParameterizedTest
    @EnumSource(MealType.class)
    @DisplayName("Should add meal for all meal types")
    void should_AddMeal_ForAllMealTypes(MealType mealType) {
        // Given
        Long userId = 1L;
        List<Long> foodIds = Arrays.asList(1L, 2L);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood1));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(testFood2));
        when(mealRepository.save(any(Meal.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Meal result = mealPlannerService.addMeal(userId, testDate, mealType, foodIds);

        // Then
        assertNotNull(result);
        assertEquals(mealType, result.getMealType());
        verify(mealRepository).save(any(Meal.class));
    }

    @Test
    @DisplayName("Should throw exception when userId is null")
    void should_ThrowException_When_UserIdIsNull() {
        // When & Then
        List<Long> foodIds = Arrays.asList(1L, 2L);
        
        InvalidInputException exception = assertThrows(
            InvalidInputException.class,
            () -> mealPlannerService.addMeal(null, testDate, MealType.BREAKFAST, foodIds)
        );

        assertEquals("userId must not be null", exception.getMessage());
        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Should throw exception when foods list is empty")
    void should_ThrowException_When_FoodsListIsEmpty() {
        // Given
        Long userId = 1L;
        List<Long> emptyFoodIds = Collections.emptyList();

        // When & Then
        InvalidInputException exception = assertThrows(
            InvalidInputException.class,
            () -> mealPlannerService.addMeal(userId, testDate, MealType.LUNCH, emptyFoodIds)
        );

        assertEquals("foodIds must not be empty", exception.getMessage());
        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Should calculate nutrition for date with meals")
    void should_CalculateNutrition_When_MealsExist() {
        // Given
        Long userId = 1L;
        Meal meal = new Meal();
        meal.setFoods(testFoods);
        List<Meal> meals = Collections.singletonList(meal);
        
        NutritionSummary expectedSummary = new NutritionSummary(380, 31, 45, 0);

        when(mealRepository.findByUserIdAndDate(userId, testDate)).thenReturn(meals);
        when(nutritionService.calculateFromMeals(meals)).thenReturn(expectedSummary);

        // When
        NutritionSummary result = mealPlannerService.calculateNutrition(userId, testDate);

        // Then
        assertNotNull(result);
        assertEquals(380, result.getCalories());
        assertEquals(31, result.getProtein());
        assertEquals(45, result.getCarbs());
        
        verify(mealRepository).findByUserIdAndDate(userId, testDate);
        verify(nutritionService).calculateFromMeals(meals);
    }

    @Test
    @DisplayName("Should return empty nutrition when no meals exist")
    void should_ReturnEmptyNutrition_When_NoMeals() {
        // Given
        Long userId = 1L;
        when(mealRepository.findByUserIdAndDate(userId, testDate))
            .thenReturn(Collections.emptyList());

        // When
        NutritionSummary result = mealPlannerService.calculateNutrition(userId, testDate);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getCalories());
        assertEquals(0, result.getProtein());
        assertEquals(0, result.getCarbs());
        assertEquals(0, result.getFats());
        
        verify(nutritionService, never()).calculateFromMeals(any());
    }

    

    @ParameterizedTest
    @ValueSource(doubles = {-100.0, -1.0, 0.0})
    @DisplayName("Should throw exception for invalid water amounts")
    void should_ThrowException_When_WaterAmountIsInvalid(double invalidAmount) {
        // Given
        Long userId = 1L;

        // When & Then
        assertThrows(
            InvalidInputException.class,
            () -> mealPlannerService.trackWaterIntake(userId, invalidAmount, testDate)
        );
        
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should set daily nutrition goals successfully")
    void should_SetDailyGoals_When_ValidInputs() {
        // Given
        Long userId = 1L;
        double calories = 2000;
        double protein = 150;
        double carbs = 250;
        double fats = 65;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        NutritionGoal result = mealPlannerService.setDailyGoals(userId, calories, protein, carbs, fats);

        // Then
        assertNotNull(result);
        assertEquals(2000, result.getCalories());
        assertEquals(150, result.getProtein());
        assertEquals(250, result.getCarbs());
        assertEquals(65, result.getFats());
        
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when generating shopping list for invalid week")
    void should_ThrowException_When_WeekNumberIsInvalid() {
        // Given
        Long userId = 1L;
        int invalidWeek = 53;

        // When & Then
        assertThrows(
            InvalidInputException.class,
            () -> mealPlannerService.generateShoppingList(userId, invalidWeek)
        );
        
        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Should generate shopping list for valid week")
    void should_GenerateShoppingList_When_ValidWeek() {
        // Given
        Long userId = 1L;
        int weekNumber = 10;
        
        Meal meal1 = new Meal();
        meal1.setFoods(Collections.singletonList(testFood1));
        
        Meal meal2 = new Meal();
        meal2.setFoods(Collections.singletonList(testFood2));
        
        List<Meal> meals = Arrays.asList(meal1, meal2);

        when(mealRepository.findByUserIdAndDateBetween(
            eq(userId), any(LocalDate.class), any(LocalDate.class))
        ).thenReturn(meals);

        // When
        List<String> result = mealPlannerService.generateShoppingList(userId, weekNumber);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Chicken"));
        assertTrue(result.contains("Rice"));
        
        verify(mealRepository).findByUserIdAndDateBetween(
            eq(userId), any(LocalDate.class), any(LocalDate.class)
        );
    }

    @Test
    @DisplayName("Should suggest meals based on preferences")
    void should_SuggestMeals_When_ValidPreferences() {
        // Given
        Long userId = 1L;
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("diet", "vegetarian");
        preferences.put("maxCalories", 500);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(nutritionService.findSuitableMeals(eq(testUser), eq(preferences)))
            .thenReturn(testFoods);

        // When
        List<Food> result = mealPlannerService.suggestMeals(userId, preferences);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(nutritionService).findSuitableMeals(testUser, preferences);
    }

    @Test
    @DisplayName("Should throw exception when preferences is null")
    void should_ThrowException_When_PreferencesIsNull() {
        // Given
        Long userId = 1L;

        // When & Then
        assertThrows(
            InvalidInputException.class,
            () -> mealPlannerService.suggestMeals(userId, null)
        );
        
        verifyNoInteractions(nutritionService);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void should_ThrowException_When_UserNotFound() {
        // Given
        Long userId = 999L;
        MealType mealType = MealType.BREAKFAST;
        List<Long> foodIds = Arrays.asList(1L, 2L);
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> mealPlannerService.addMeal(userId, testDate, mealType, foodIds)
        );
        
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Should throw exception when date is null")
    void should_ThrowException_When_DateIsNull() {
        Long userId = 1L;
        MealType mealType = MealType.BREAKFAST;
        List<Long> foodIds = Arrays.asList(1L, 2L);

        assertThrows(
            InvalidInputException.class,
            () -> mealPlannerService.addMeal(userId, null, mealType, foodIds)
        );
        
        verifyNoInteractions(mealRepository, userRepository);
    }

    @Test
    @DisplayName("Should calculate nutrition for multiple meals in a day")
    void should_CalculateNutrition_When_MultipleMealsExist() {
        // Given
        Long userId = 1L;
        Meal breakfast = new Meal();
        breakfast.setMealType(MealType.BREAKFAST);
        breakfast.setFoods(testFoods);

        Meal lunch = new Meal();
        lunch.setMealType(MealType.LUNCH);
        lunch.setFoods(testFoods);

        List<Meal> meals = Arrays.asList(breakfast, lunch);
        
        // Expected nutrition is doubled since we have two identical meals
        NutritionSummary expectedSummary = new NutritionSummary(760, 62, 90, 0);

        when(mealRepository.findByUserIdAndDate(userId, testDate)).thenReturn(meals);
        when(nutritionService.calculateFromMeals(meals)).thenReturn(expectedSummary);

        // When
        NutritionSummary result = mealPlannerService.calculateNutrition(userId, testDate);

        // Then
        assertNotNull(result);
        assertEquals(760, result.getCalories());
        assertEquals(62, result.getProtein());
        assertEquals(90, result.getCarbs());
        
        verify(mealRepository).findByUserIdAndDate(userId, testDate);
        verify(nutritionService).calculateFromMeals(meals);
    }

    @Test
    @DisplayName("Should throw exception when tracking water with null date")
    void should_ThrowException_When_TrackingWaterWithNullDate() {
        // Given
        Long userId = 1L;
        double amount = 500.0;

        // When & Then
        assertThrows(
            InvalidInputException.class,
            () -> mealPlannerService.trackWaterIntake(userId, amount, null)
        );
        
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should update water intake for same date")
    void should_UpdateWaterIntake_When_EntryExistsForDate() {
        // Given
        Long userId = 1L;
        double initialAmount = 500.0;
        double additionalAmount = 250.0;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        mealPlannerService.trackWaterIntake(userId, initialAmount, testDate);
        mealPlannerService.trackWaterIntake(userId, additionalAmount, testDate);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertEquals(750.0, savedUser.getWaterIntake().get(testDate));
    }

    @ParameterizedTest
    @CsvSource({
        "-2000,150,250,65",
        "2000,-150,250,65",
        "2000,150,-250,65",
        "2000,150,250,-65"
    })
    @DisplayName("Should throw exception when nutrition goals have negative values")
    void should_ThrowException_When_NutritionGoalsAreNegative(
            double calories, double protein, double carbs, double fats) {
        // Given
        Long userId = 1L;

        // When & Then
        assertThrows(
            InvalidInputException.class,
            () -> mealPlannerService.setDailyGoals(userId, calories, protein, carbs, fats)
        );
        
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw exception when user not found for goals")
    void should_ThrowException_When_UserNotFoundForGoals() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
            RuntimeException.class,
            () -> mealPlannerService.setDailyGoals(userId, 2000, 150, 250, 65)
        );
        
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return empty list for week with no meals")
    void should_ReturnEmptyList_When_NoMealsInWeek() {
        // Given
        Long userId = 1L;
        int weekNumber = 10;

        when(mealRepository.findByUserIdAndDateBetween(
            eq(userId), any(LocalDate.class), any(LocalDate.class))
        ).thenReturn(Collections.emptyList());

        // When
        List<String> result = mealPlannerService.generateShoppingList(userId, weekNumber);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(mealRepository).findByUserIdAndDateBetween(
            eq(userId), any(LocalDate.class), any(LocalDate.class)
        );
    }

    @Test
    @DisplayName("Should deduplicate ingredients in shopping list")
    void should_DeduplicateIngredients_When_GeneratingShoppingList() {
        // Given
        Long userId = 1L;
        int weekNumber = 10;
        
        Food food1 = new Food();
        food1.getIngredients().add("Rice");
        food1.getIngredients().add("Chicken");
        
        Food food2 = new Food();
        food2.getIngredients().add("Rice"); // Duplicate ingredient
        food2.getIngredients().add("Vegetables");
        
        Meal meal1 = new Meal();
        meal1.setFoods(Collections.singletonList(food1));
        
        Meal meal2 = new Meal();
        meal2.setFoods(Collections.singletonList(food2));
        
        List<Meal> meals = Arrays.asList(meal1, meal2);

        when(mealRepository.findByUserIdAndDateBetween(
            eq(userId), any(LocalDate.class), any(LocalDate.class))
        ).thenReturn(meals);

        // When
        List<String> result = mealPlannerService.generateShoppingList(userId, weekNumber);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Rice"));
        assertTrue(result.contains("Chicken"));
        assertTrue(result.contains("Vegetables"));
        
        verify(mealRepository).findByUserIdAndDateBetween(
            eq(userId), any(LocalDate.class), any(LocalDate.class)
        );
    }

    @Test
    @DisplayName("Should throw exception when preferences map is empty")
    void should_ThrowException_When_PreferencesMapIsEmpty() {
        // Given
        Long userId = 1L;
        Map<String, Object> emptyPreferences = new HashMap<>();

        // When & Then
        assertThrows(
            InvalidInputException.class,
            () -> mealPlannerService.suggestMeals(userId, emptyPreferences)
        );
        
        verifyNoInteractions(nutritionService);
    }

    @Test
    @DisplayName("Should handle all meal type default times")
    void should_HandleAllBranches_In_MealTypeDefaultTime() {
        // Test all branches in getMealTypeDefaultTime
        Long userId = 1L;
        List<Long> foodIds = Arrays.asList(1L, 2L);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(testFood1));
        when(mealRepository.save(any(Meal.class))).thenAnswer(i -> i.getArgument(0));

        // Test BREAKFAST (8:00)
        Meal breakfastMeal = mealPlannerService.addMeal(userId, testDate, MealType.BREAKFAST, foodIds);
        assertEquals(8, breakfastMeal.getMealTime().getHour());
        assertEquals(0, breakfastMeal.getMealTime().getMinute());

        // Test LUNCH (12:00)
        Meal lunchMeal = mealPlannerService.addMeal(userId, testDate, MealType.LUNCH, foodIds);
        assertEquals(12, lunchMeal.getMealTime().getHour());
        assertEquals(0, lunchMeal.getMealTime().getMinute());

        // Test DINNER (19:00)
        Meal dinnerMeal = mealPlannerService.addMeal(userId, testDate, MealType.DINNER, foodIds);
        assertEquals(19, dinnerMeal.getMealTime().getHour());
        assertEquals(0, dinnerMeal.getMealTime().getMinute());

        // Test SNACK (15:00)
        Meal snackMeal = mealPlannerService.addMeal(userId, testDate, MealType.SNACK, foodIds);
        assertEquals(15, snackMeal.getMealTime().getHour());
        assertEquals(0, snackMeal.getMealTime().getMinute());
    }

    @Test
    @DisplayName("Should handle all validation conditions separately")
    void should_HandleAllBranches_In_Validation() {
        // Test each validation condition separately
        Long userId = 1L;
        LocalDate date = LocalDate.now();
        List<Long> foodIds = Arrays.asList(1L, 2L);

        // Test null userId
        assertThrows(InvalidInputException.class, () ->
            mealPlannerService.addMeal(null, date, MealType.BREAKFAST, foodIds));

        // Test null date
        assertThrows(InvalidInputException.class, () ->
            mealPlannerService.addMeal(userId, null, MealType.BREAKFAST, foodIds));

        // Test null mealType
        assertThrows(InvalidInputException.class, () ->
            mealPlannerService.addMeal(userId, date, null, foodIds));

        // Test null foodIds
        assertThrows(InvalidInputException.class, () ->
            mealPlannerService.addMeal(userId, date, MealType.BREAKFAST, null));

        // Test empty foodIds
        assertThrows(InvalidInputException.class, () ->
            mealPlannerService.addMeal(userId, date, MealType.BREAKFAST, Collections.emptyList()));

        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Should handle duplicate foods in meal")
    void should_HandleDuplicateFoods_InMeal() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood1));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(testFood2));
        when(mealRepository.save(any(Meal.class))).thenAnswer(i -> i.getArgument(0));

        // When
        List<Long> foodIdsWithDuplicates = Arrays.asList(1L, 1L, 2L);
        Meal result = mealPlannerService.addMeal(userId, testDate, MealType.LUNCH, foodIdsWithDuplicates);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getFoods().size());
        
        verify(foodRepository, times(2)).findById(1L);
        verify(foodRepository).findById(2L);
        verify(mealRepository).save(any(Meal.class));
    }

    @Test
    @DisplayName("Should handle nutrition calculation edge cases")
    void should_HandleEdgeCases_In_NutritionCalculation() {
        Long userId = 1L;
        
        // Test calculation with no meals
        when(mealRepository.findByUserIdAndDate(userId, testDate))
            .thenReturn(Collections.emptyList());
            
        NutritionSummary emptyResult = mealPlannerService.calculateNutrition(userId, testDate);
        assertNotNull(emptyResult);
        assertEquals(0, emptyResult.getCalories());
        assertEquals(0, emptyResult.getProtein());
        assertEquals(0, emptyResult.getCarbs());
        assertEquals(0, emptyResult.getFats());

        // Test calculation with multiple meals
        Meal meal1 = new Meal();
        meal1.setFoods(Arrays.asList(testFood1));
        
        Meal meal2 = new Meal();
        meal2.setFoods(Arrays.asList(testFood2));
        
        when(mealRepository.findByUserIdAndDate(userId, testDate))
            .thenReturn(Arrays.asList(meal1, meal2));
            
        NutritionSummary expectedSummary = new NutritionSummary(380, 31, 45, 0);
        when(nutritionService.calculateFromMeals(any())).thenReturn(expectedSummary);

        NutritionSummary result = mealPlannerService.calculateNutrition(userId, testDate);
        assertNotNull(result);
        assertEquals(380, result.getCalories());
        assertEquals(31, result.getProtein());
        assertEquals(45, result.getCarbs());
        
        verify(mealRepository, times(2)).findByUserIdAndDate(eq(userId), eq(testDate));
        verify(nutritionService).calculateFromMeals(any());
    }
    
    @Test
    @DisplayName("Should process meal with various food combinations")
    void should_ProcessMeal_WithVariousFoodCombinations() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        Food food3 = new Food();
        food3.setId(3L);
        food3.setName("Salad");
        
        Food food4 = new Food();
        food4.setId(4L);
        food4.setName("Dressing");
        
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood1));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(testFood2));
        when(foodRepository.findById(3L)).thenReturn(Optional.of(food3));
        when(foodRepository.findById(4L)).thenReturn(Optional.of(food4));
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> {
            Meal savedMeal = invocation.getArgument(0);
            if (savedMeal != null) {
                savedMeal.setId(1L); // Set an ID for the saved meal
            }
            return savedMeal;
        });
        
        // Test with one food
        List<Long> singleFoodIds = Collections.singletonList(1L);
        Meal singleFoodMeal = mealPlannerService.addMeal(userId, testDate, MealType.BREAKFAST, singleFoodIds);
        assertNotNull(singleFoodMeal, "Meal should not be null");
        assertEquals(1L, singleFoodMeal.getId());
        assertTrue(singleFoodMeal.getName().startsWith("Breakfast"));
        assertTrue(singleFoodMeal.getName().contains("Chicken Breast"));
        assertEquals(1, singleFoodMeal.getFoods().size());
        assertEquals(testFood1, singleFoodMeal.getFoods().get(0));
        
        // Test with multiple foods
        List<Long> multipleFoodIds = Arrays.asList(1L, 2L, 3L, 4L);
        Meal multipleFoodMeal = mealPlannerService.addMeal(userId, testDate, MealType.LUNCH, multipleFoodIds);
        assertNotNull(multipleFoodMeal, "Meal with multiple foods should not be null");
        assertEquals(1L, multipleFoodMeal.getId());
        assertTrue(multipleFoodMeal.getName().startsWith("Lunch"));
        assertTrue(multipleFoodMeal.getName().contains("Chicken Breast"));
        assertTrue(multipleFoodMeal.getName().contains("Brown Rice"));
        assertTrue(multipleFoodMeal.getName().contains("Salad"));
        assertTrue(multipleFoodMeal.getName().contains("..."));
        assertEquals(4, multipleFoodMeal.getFoods().size());
        
        verify(foodRepository, times(2)).findById(1L);
        verify(foodRepository).findById(2L);
        verify(foodRepository).findById(3L);
        verify(foodRepository).findById(4L);
        verify(mealRepository, times(2)).save(any(Meal.class));
    }

    @Test
    @DisplayName("Should update dietary preferences correctly")
    void should_UpdateDietaryPreferences_Successfully() {
        // Given
        Long userId = 1L;
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("diet", "vegan");
        preferences.put("allergies", Arrays.asList("nuts", "dairy"));
        preferences.put("maxCalories", 2000);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(nutritionService.findSuitableMeals(eq(testUser), eq(preferences)))
            .thenReturn(Collections.singletonList(testFood1));
        
        // When
        List<Food> result = mealPlannerService.suggestMeals(userId, preferences);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFood1, result.get(0));
        
        verify(nutritionService).findSuitableMeals(testUser, preferences);
    }

    @Test
    @DisplayName("Should handle food not found scenario")
    void should_ThrowException_When_FoodNotFound() {
        // Given
        Long userId = 1L;
        Long nonExistentFoodId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(foodRepository.findById(nonExistentFoodId)).thenReturn(Optional.empty());
        
        // When & Then
        FoodNotFoundException exception = assertThrows(
            FoodNotFoundException.class,
            () -> mealPlannerService.addMeal(userId, testDate, MealType.BREAKFAST, Arrays.asList(nonExistentFoodId))
        );
        
        assertEquals("Food not found with id: " + nonExistentFoodId, exception.getMessage());
        verify(foodRepository).findById(nonExistentFoodId);
        verify(mealRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should track water intake for multiple dates")
    void should_TrackWaterIntake_ForMultipleDates() {
        // Given
        Long userId = 1L;
        LocalDate date1 = testDate;
        LocalDate date2 = testDate.plusDays(1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        mealPlannerService.trackWaterIntake(userId, 500.0, date1);
        mealPlannerService.trackWaterIntake(userId, 750.0, date2);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(userCaptor.capture());
        
        List<User> savedUsers = userCaptor.getAllValues();
        Map<LocalDate, Double> waterIntake = savedUsers.get(1).getWaterIntake();
        assertEquals(500.0, waterIntake.get(date1));
        assertEquals(750.0, waterIntake.get(date2));
    }

    @Test
    @DisplayName("Should update dietary preferences with all types")
    void should_UpdateDietaryPreferences_WithAllTypes() {
        // Given
        Long userId = 1L;
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("diet", "vegan");
        preferences.put("allergies", Arrays.asList("nuts", "dairy"));
        preferences.put("maxCalories", 2000);
        preferences.put("minProtein", 50);
        preferences.put("excludeIngredients", Arrays.asList("soy", "gluten"));
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        List<Food> suitableFoods = Arrays.asList(testFood1, testFood2);
        when(nutritionService.findSuitableMeals(eq(testUser), eq(preferences)))
            .thenReturn(suitableFoods);
        
        // When
        List<Food> result = mealPlannerService.suggestMeals(userId, preferences);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(suitableFoods, result);
        verify(nutritionService).findSuitableMeals(testUser, preferences);
    }

    @Test
    @DisplayName("Should validate week number range for shopping list")
    void should_ValidateWeekNumber_ForShoppingList() {
        // Given
        Long userId = 1L;
        
        // Test with week 0
        assertThrows(InvalidInputException.class,
            () -> mealPlannerService.generateShoppingList(userId, 0));
        
        // Test with week 53
        assertThrows(InvalidInputException.class,
            () -> mealPlannerService.generateShoppingList(userId, 53));
        
        // Test with valid week numbers
        when(mealRepository.findByUserIdAndDateBetween(
            eq(userId), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Collections.emptyList());
            
        assertDoesNotThrow(() -> {
            mealPlannerService.generateShoppingList(userId, 1);
            mealPlannerService.generateShoppingList(userId, 52);
        });
        
        verify(mealRepository, times(2))
            .findByUserIdAndDateBetween(eq(userId), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Should generate meal name with edge cases")
    void should_GenerateMealName_WithEdgeCases() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(testFood1));
        when(mealRepository.save(any(Meal.class))).thenAnswer(i -> i.getArgument(0));
        
        // Test empty food list
        List<Long> emptyFoodIds = Collections.emptyList();
        assertThrows(InvalidInputException.class,
            () -> mealPlannerService.addMeal(userId, testDate, MealType.BREAKFAST, emptyFoodIds));
        
        // Test single food
        List<Long> singleFoodId = Collections.singletonList(1L);
        Meal singleFoodMeal = mealPlannerService.addMeal(userId, testDate, MealType.BREAKFAST, singleFoodId);
        assertTrue(singleFoodMeal.getName().contains(testFood1.getName()));
        
        // Test exact three foods
        List<Long> threeFoodIds = Arrays.asList(1L, 1L, 1L);
        Meal threeFoodsMeal = mealPlannerService.addMeal(userId, testDate, MealType.LUNCH, threeFoodIds);
        assertEquals(3, threeFoodsMeal.getFoods().size());
        assertFalse(threeFoodsMeal.getName().contains("..."));
        
        // Test more than three foods
        List<Long> manyFoodIds = Arrays.asList(1L, 1L, 1L, 1L);
        Meal manyFoodsMeal = mealPlannerService.addMeal(userId, testDate, MealType.DINNER, manyFoodIds);
        assertTrue(manyFoodsMeal.getName().contains("..."));
        assertEquals(4, manyFoodsMeal.getFoods().size());
    }
}