package com.example.mealplanner.dto;

import com.example.mealplanner.model.MealType;
import java.time.LocalDate;
import java.util.List;

public class AddMealRequest {
    private Long userId;
    private LocalDate date;
    private MealType mealType;
    private List<Long> foodIds;

    public AddMealRequest(Long userId, LocalDate date, MealType mealType, List<Long> foodIds) {
        this.userId = userId;
        this.date = date;
        this.mealType = mealType;
        this.foodIds = foodIds;
    }

    // Getters
    public Long getUserId() { return userId; }
    public LocalDate getDate() { return date; }
    public MealType getMealType() { return mealType; }
    public List<Long> getFoodIds() { return foodIds; }
}