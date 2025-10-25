package com.example.mealplanner.service;

import com.example.mealplanner.dto.NutritionSummary;
import com.example.mealplanner.model.Food;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.User;
import com.example.mealplanner.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NutritionService {
    
    private final FoodRepository foodRepository;
    
    public NutritionService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }
    
    public NutritionSummary calculateFromMeals(List<Meal> meals) {
        if (meals == null || meals.isEmpty()) {
            return NutritionSummary.empty();
        }

        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFats = 0;
        
        for (Meal meal : meals) {
            if (meal == null || meal.getFoods() == null) continue;
            for (Food food : meal.getFoods()) {
                if (food == null) continue;
                totalCalories += food.getCalories();
                totalProtein += food.getProtein();
                totalCarbs += food.getCarbs();
                totalFats += food.getFats();
            }
        }
        
        return new NutritionSummary(totalCalories, totalProtein, totalCarbs, totalFats);
    }
    
    public List<Food> findSuitableMeals(User user, Map<String, Object> preferences) {
        if (user == null || preferences == null || preferences.isEmpty()) {
            throw new IllegalArgumentException("User and preferences are required");
        }

        validatePreferences(preferences);

        return foodRepository.findAll().stream()
            .filter(food -> food != null)
            .filter(food -> meetsPreferences(food, preferences))
            .toList();
    }

    private void validatePreferences(Map<String, Object> preferences) {
        for (Map.Entry<String, Object> entry : preferences.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case "maxCalories" -> {
                    if (!(value instanceof Number) || ((Number) value).doubleValue() <= 0) {
                        throw new IllegalArgumentException("maxCalories must be a positive number");
                    }
                }
                case "minProtein" -> {
                    if (!(value instanceof Number) || ((Number) value).doubleValue() <= 0) {
                        throw new IllegalArgumentException("minProtein must be a positive number");
                    }
                }
                case "maxCarbs" -> {
                    if (!(value instanceof Number) || ((Number) value).doubleValue() <= 0) {
                        throw new IllegalArgumentException("maxCarbs must be a positive number");
                    }
                }
                case "diet" -> {
                    if (!(value instanceof String)) {
                        throw new IllegalArgumentException("Diet preference must be a string");
                    }
                    String diet = ((String) value).toLowerCase();
                    if (!diet.matches("vegetarian|vegan|pescatarian")) {
                        throw new IllegalArgumentException("Invalid diet type: " + value);
                    }
                }
                default -> throw new IllegalArgumentException("Unknown preference: " + key);
            }
        }
    }

    private boolean meetsDietaryPreference(Food food, Map<String, Object> preferences) {
        String diet = (String) preferences.get("diet");
        if (diet == null) return true;

        List<String> ingredients = food.getIngredients().stream()
            .map(String::toLowerCase)
            .toList();

        return switch (diet.toLowerCase()) {
            case "vegetarian" -> ingredients.stream()
                .noneMatch(ingredient ->
                    ingredient.contains("chicken") ||
                    ingredient.contains("beef") ||
                    ingredient.contains("fish") ||
                    ingredient.contains("pork") ||
                    ingredient.contains("meat"));
            case "vegan" -> ingredients.stream()
                .noneMatch(ingredient -> 
                    ingredient.contains("chicken") ||
                    ingredient.contains("beef") ||
                    ingredient.contains("fish") ||
                    ingredient.contains("pork") ||
                    ingredient.contains("meat") ||
                    ingredient.contains("egg") ||
                    ingredient.contains("milk") ||
                    ingredient.contains("dairy") ||
                    ingredient.contains("cheese") ||
                    ingredient.contains("honey"));
            case "pescatarian" -> ingredients.stream()
                .noneMatch(ingredient -> 
                    ingredient.contains("chicken") ||
                    ingredient.contains("beef") ||
                    ingredient.contains("pork") ||
                    ingredient.contains("meat"));
            default -> false; // Invalid diet type should never pass validation
        };
    }

    private boolean meetsCaloriePreference(Food food, Map<String, Object> preferences) {
        Number maxCalories = (Number) preferences.get("maxCalories");
        if (maxCalories == null) return true;
        
        double maxCalValue = maxCalories.doubleValue();
        return maxCalValue > 0 && food.getCalories() <= maxCalValue;
    }

    private boolean meetsProteinPreference(Food food, Map<String, Object> preferences) {
        Number minProtein = (Number) preferences.get("minProtein");
        if (minProtein == null) return true;
        
        double minProtValue = minProtein.doubleValue();
        return minProtValue > 0 && food.getProtein() >= minProtValue;
    }

    private boolean meetsCarbsPreference(Food food, Map<String, Object> preferences) {
        Number maxCarbs = (Number) preferences.get("maxCarbs");
        if (maxCarbs == null) return true;
        
        double maxCarbsValue = maxCarbs.doubleValue();
        return maxCarbsValue > 0 && food.getCarbs() <= maxCarbsValue;
    }

    private boolean meetsPreferences(Food food, Map<String, Object> preferences) {
        if (food == null) return false;
        
        // If any specified preference is not met, return false
        if (preferences.containsKey("diet") && !meetsDietaryPreference(food, preferences)) {
            return false;
        }
        
        if (preferences.containsKey("maxCalories") && !meetsCaloriePreference(food, preferences)) {
            return false;
        }
        
        if (preferences.containsKey("minProtein") && !meetsProteinPreference(food, preferences)) {
            return false;
        }
        
        if (preferences.containsKey("maxCarbs") && !meetsCarbsPreference(food, preferences)) {
            return false;
        }
        
        return true;
    }
}