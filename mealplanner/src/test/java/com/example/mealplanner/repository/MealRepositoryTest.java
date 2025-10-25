// package com.example.mealplanner.repository;

// import com.example.mealplanner.model.*;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

// import java.time.LocalDate;
// import java.util.Arrays;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest
// @DisplayName("Meal Repository Tests")
// class MealRepositoryTest {

//     @Autowired
//     private TestEntityManager entityManager;

//     @Autowired
//     private MealRepository mealRepository;

//     private User testUser;
//     private Food testFood1;
//     private Food testFood2;
//     private Meal testMeal;
//     private LocalDate testDate;

//     @BeforeEach
//     void setUp() {
//         testDate = LocalDate.now();

//         // Create and persist user
//         testUser = new User();
//         testUser.setName("John Doe");
//         testUser.setEmail("john@example.com");
//         entityManager.persist(testUser);

//         // Create and persist foods
//         testFood1 = new Food();
//         testFood1.setName("Chicken Breast");
//         testFood1.setCalories(165);
//         entityManager.persist(testFood1);

//         testFood2 = new Food();
//         testFood2.setName("Brown Rice");
//         testFood2.setCalories(215);
//         entityManager.persist(testFood2);

//         // Create and persist meal
//         testMeal = new Meal();
//         testMeal.setUser(testUser);
//         testMeal.setDate(testDate);
//         testMeal.setName("Lunch Meal");
//         testMeal.setMealType(MealType.LUNCH);
//         testMeal.setFoods(Arrays.asList(testFood1, testFood2));
//         entityManager.persist(testMeal);

//         entityManager.flush();
//     }

//     @Test
//     @DisplayName("Should find meals by user ID and date")
//     void should_FindMeals_ByUserIdAndDate() {
//         // When
//         List<Meal> foundMeals = mealRepository.findByUserIdAndDate(testUser.getId(), testDate);

//         // Then
//         assertNotNull(foundMeals);
//         assertFalse(foundMeals.isEmpty());
//         assertEquals(1, foundMeals.size());
        
//         Meal foundMeal = foundMeals.get(0);
//         assertEquals(testUser.getId(), foundMeal.getUser().getId());
//         assertEquals(testDate, foundMeal.getDate());
//         assertEquals(MealType.LUNCH, foundMeal.getMealType());
//         assertEquals(2, foundMeal.getFoods().size());
//     }

//     @Test
//     @DisplayName("Should find meals by user ID and date range")
//     void should_FindMeals_ByUserIdAndDateRange() {
//         // Given
//         LocalDate startDate = testDate.minusDays(1);
//         LocalDate endDate = testDate.plusDays(1);

//         // When
//         List<Meal> foundMeals = mealRepository.findByUserIdAndDateBetween(
//             testUser.getId(), startDate, endDate);

//         // Then
//         assertNotNull(foundMeals);
//         assertFalse(foundMeals.isEmpty());
//         assertEquals(1, foundMeals.size());
        
//         Meal foundMeal = foundMeals.get(0);
//         assertTrue(foundMeal.getDate().isEqual(testDate));
//     }

//     @Test
//     @DisplayName("Should return empty list when no meals found")
//     void should_ReturnEmptyList_WhenNoMealsFound() {
//         // Given
//         LocalDate futureDate = testDate.plusDays(7);

//         // When
//         List<Meal> foundMeals = mealRepository.findByUserIdAndDate(testUser.getId(), futureDate);

//         // Then
//         assertNotNull(foundMeals);
//         assertTrue(foundMeals.isEmpty());
//     }

//     @Test
//     @DisplayName("Should return empty list for non-existent user")
//     void should_ReturnEmptyList_ForNonExistentUser() {
//         // Given
//         Long nonExistentUserId = 999L;

//         // When
//         List<Meal> foundMeals = mealRepository.findByUserIdAndDate(nonExistentUserId, testDate);

//         // Then
//         assertNotNull(foundMeals);
//         assertTrue(foundMeals.isEmpty());
//     }

//     @Test
//     @DisplayName("Should find meals for multiple days")
//     void should_FindMeals_ForMultipleDays() {
//         // Given
//         Meal tomorrowMeal = new Meal();
//         tomorrowMeal.setUser(testUser);
//         tomorrowMeal.setDate(testDate.plusDays(1));
//         tomorrowMeal.setName("Breakfast Meal");
//         tomorrowMeal.setMealType(MealType.BREAKFAST);
//         tomorrowMeal.setFoods(Arrays.asList(testFood1));
//         entityManager.persist(tomorrowMeal);
//         entityManager.flush();

//         // When
//         List<Meal> foundMeals = mealRepository.findByUserIdAndDateBetween(
//             testUser.getId(), testDate, testDate.plusDays(1));

//         // Then
//         assertNotNull(foundMeals);
//         assertEquals(2, foundMeals.size());
//     }

//     @Test
//     @DisplayName("Should save meal with all relationships")
//     void should_SaveMeal_WithAllRelationships() {
//         // Given
//         Meal newMeal = new Meal();
//         newMeal.setUser(testUser);
//         newMeal.setDate(testDate.plusDays(1));
//         newMeal.setName("Dinner Meal");
//         newMeal.setMealType(MealType.DINNER);
//         newMeal.setFoods(Arrays.asList(testFood1, testFood2));

//         // When
//         Meal savedMeal = mealRepository.save(newMeal);
//         entityManager.flush();
//         entityManager.clear();

//         // Then
//         Meal foundMeal = entityManager.find(Meal.class, savedMeal.getId());
//         assertNotNull(foundMeal);
//         assertEquals(testUser.getId(), foundMeal.getUser().getId());
//         assertEquals(2, foundMeal.getFoods().size());
//         assertEquals(MealType.DINNER, foundMeal.getMealType());
//     }
// }