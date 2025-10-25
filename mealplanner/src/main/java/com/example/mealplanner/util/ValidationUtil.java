package com.example.mealplanner.util;

import static com.example.mealplanner.exception.MealPlannerExceptions.InvalidInputException;
import java.util.List;

public class ValidationUtil {
    
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidInputException(fieldName + " must not be null");
        }
    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " must not be empty");
        }
    }

    public static void validateNotEmpty(List<?> list, String fieldName) {
        if (list == null || list.isEmpty()) {
            throw new InvalidInputException(fieldName + " must not be empty");
        }
    }

    public static void validatePositive(Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            throw new InvalidInputException(fieldName + " must be positive");
        }
    }

    public static void validateMinValue(Number value, Number minValue, String fieldName) {
        if (value == null || value.doubleValue() < minValue.doubleValue()) {
            throw new InvalidInputException(fieldName + " must be at least " + minValue);
        }
    }

    public static void validateMaxValue(Number value, Number maxValue, String fieldName) {
        if (value == null || value.doubleValue() > maxValue.doubleValue()) {
            throw new InvalidInputException(fieldName + " must not exceed " + maxValue);
        }
    }
}