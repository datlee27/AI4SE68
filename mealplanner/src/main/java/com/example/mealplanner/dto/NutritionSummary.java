package com.example.mealplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NutritionSummary {
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    
    public NutritionSummary(double calories, double protein, double carbs, double fats) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }
    
    public NutritionSummary(int calories, int protein, int carbs, int fats) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }
    
    public static NutritionSummary empty() {
        return new NutritionSummary(0.0, 0.0, 0.0, 0.0);
    }
}