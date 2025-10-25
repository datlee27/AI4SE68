package com.example.mealplanner.controller;

import com.example.mealplanner.model.*;
import com.example.mealplanner.service.MealPlannerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MealController.class)
@DisplayName("Meal Controller Tests")
class MealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealPlannerService mealPlannerService;

    private User testUser;
    private Food testFood1;
    private Food testFood2;
    private List<Food> testFoods;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");

        testFood1 = new Food();
        testFood1.setId(1L);
        testFood1.setName("Chicken Breast");

        testFood2 = new Food();
        testFood2.setId(2L);
        testFood2.setName("Brown Rice");

        testFoods = Arrays.asList(testFood1, testFood2);
        testDate = LocalDate.now();
    }

    @Test
    @DisplayName("Should show add meal form")
    void should_ShowAddMealForm() throws Exception {
        mockMvc.perform(get("/meals/add"))
               .andExpect(status().isOk())
               .andExpect(view().name("meal-form"))
               .andExpect(model().attributeExists("mealTypes"))
               .andExpect(model().attributeExists("today"));
    }

    @Test
    @DisplayName("Should add meal successfully")
    void should_AddMeal_Successfully() throws Exception {
        Meal expectedMeal = new Meal();
        expectedMeal.setId(1L);
        expectedMeal.setUser(testUser);
        expectedMeal.setDate(testDate);
        expectedMeal.setMealType(MealType.BREAKFAST);
        expectedMeal.setFoods(testFoods);

        when(mealPlannerService.addMeal(eq(1L), eq(testDate), 
                                      eq(MealType.BREAKFAST), any()))
            .thenReturn(expectedMeal);

        mockMvc.perform(post("/meals/add")
                .param("userId", "1")
                .param("date", testDate.format(java.time.format.DateTimeFormatter.ISO_DATE))
                .param("mealType", "BREAKFAST")
                .param("foods", "1", "2"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/meals"));

        verify(mealPlannerService).addMeal(eq(1L), eq(testDate), 
                                         eq(MealType.BREAKFAST), any());
    }

    @Test
    @DisplayName("Should handle invalid meal submission")
    void should_HandleInvalidMealSubmission() throws Exception {
        mockMvc.perform(post("/meals/add"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should show meals list")
    void should_ShowMealsList() throws Exception {
        mockMvc.perform(get("/meals"))
               .andExpect(status().isOk())
               .andExpect(view().name("meals"));
    }

    @Test
    @DisplayName("Should handle missing user ID")
    void should_HandleMissingUserId() throws Exception {
        mockMvc.perform(post("/meals/add")
                .param("date", testDate.toString())
                .param("mealType", "BREAKFAST")
                .param("foods", "1", "2"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing meal type")
    void should_HandleMissingMealType() throws Exception {
        mockMvc.perform(post("/meals/add")
                .param("userId", "1")
                .param("date", testDate.toString())
                .param("foods", "1", "2"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid food IDs")
    void should_HandleInvalidFoodIds() throws Exception {
        mockMvc.perform(post("/meals/add")
                .param("userId", "1")
                .param("date", testDate.toString())
                .param("mealType", "BREAKFAST")
                .param("foods", "invalid"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle service exceptions")
    void should_HandleServiceExceptions() throws Exception {
        when(mealPlannerService.addMeal(any(), any(), any(), any()))
            .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/meals/add")
                .param("userId", "1")
                .param("date", testDate.toString())
                .param("mealType", "BREAKFAST")
                .param("foods", "1", "2"))
               .andExpect(status().isInternalServerError());
    }
}