package com.example.mealplanner.exception;

public class MealPlannerException extends RuntimeException {
    protected MealPlannerException(String message) {
        super(message);
    }

    public static MealPlannerException wrapException(String message, Exception e) {
        return new MealPlannerException(message + ": " + e.getMessage());
    }
}