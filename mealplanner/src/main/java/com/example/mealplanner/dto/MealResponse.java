package com.example.mealplanner.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MealResponse {
    private Long id;
    private Long userId;
    private List<FoodResponse> foods;
    private LocalDateTime mealTime;
    private String note;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<FoodResponse> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodResponse> foods) {
        this.foods = foods;
    }

    public LocalDateTime getMealTime() {
        return mealTime;
    }

    public void setMealTime(LocalDateTime mealTime) {
        this.mealTime = mealTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}