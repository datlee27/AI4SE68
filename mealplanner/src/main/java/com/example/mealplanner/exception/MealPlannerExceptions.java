package com.example.mealplanner.exception;

public class MealPlannerExceptions {
    public static class UserNotFoundException extends MealPlannerException {
        public UserNotFoundException(Long userId) {
            super("User not found with id: " + userId);
        }
    }

    public static class FoodNotFoundException extends MealPlannerException {
        public FoodNotFoundException(Long foodId) {
            super("Food not found with id: " + foodId);
        }
    }

    public static class InvalidInputException extends MealPlannerException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}