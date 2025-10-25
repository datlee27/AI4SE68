package com.example.mealplanner.service;

import com.example.mealplanner.dto.NutritionSummary;
import com.example.mealplanner.model.*;
import com.example.mealplanner.repository.*;
import com.example.mealplanner.util.ValidationUtil;
import static com.example.mealplanner.exception.MealPlannerExceptions.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MealPlannerService {
    
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final NutritionService nutritionService;
    
    public MealPlannerService(MealRepository mealRepository, 
                             UserRepository userRepository,
                             FoodRepository foodRepository,
                             NutritionService nutritionService) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.nutritionService = nutritionService;
    }
    
    public Meal addMeal(Long userId, LocalDate date, MealType mealType, List<Long> foodIds) {
        validateMealInput(userId, date, mealType, foodIds);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        List<Food> foods = findFoodsByIds(foodIds);
        
        Meal meal = createMeal(user, date, mealType, foods);
        return mealRepository.save(meal);
    }

    private void validateMealInput(Long userId, LocalDate date, MealType mealType, List<Long> foodIds) {
        ValidationUtil.validateNotNull(userId, "userId");
        ValidationUtil.validateNotNull(date, "date");
        ValidationUtil.validateNotNull(mealType, "mealType");
        ValidationUtil.validateNotEmpty(foodIds, "foodIds");
    }

    private List<Food> findFoodsByIds(List<Long> foodIds) {
        return foodIds.stream()
            .map(id -> foodRepository.findById(id)
                .orElseThrow(() -> new FoodNotFoundException(id)))
            .toList();
    }

    private Meal createMeal(User user, LocalDate date, MealType mealType, List<Food> foods) {
        Meal meal = new Meal();
        meal.setUser(user);
        meal.setMealType(mealType);
        meal.setMealTime(date.atTime(getMealTypeDefaultTime(mealType)));
        meal.setName(generateMealName(mealType, foods));
        foods.forEach(meal::addFood);
        return meal;
    }

    private String generateMealName(MealType mealType, List<Food> foods) {
        String prefix = switch (mealType) {
            case BREAKFAST -> "Breakfast";
            case LUNCH -> "Lunch";
            case DINNER -> "Dinner";
            case SNACK -> "Snack";
            default -> "Meal";
        };

        if (foods.isEmpty()) {
            return prefix;
        }

        String foodNames = foods.stream()
            .map(Food::getName)
            .limit(3)
            .collect(Collectors.joining(", "));
        return prefix + ": " + foodNames + (foods.size() > 3 ? ", ..." : "");
    }

    private LocalTime getMealTypeDefaultTime(MealType mealType) {
        return switch (mealType) {
            case BREAKFAST -> LocalTime.of(8, 0);
            case LUNCH -> LocalTime.of(12, 0);
            case DINNER -> LocalTime.of(19, 0);
            case SNACK -> LocalTime.of(15, 0);
        };
    }
    
    public NutritionSummary calculateNutrition(Long userId, LocalDate date) {
        ValidationUtil.validateNotNull(userId, "userId");
        ValidationUtil.validateNotNull(date, "date");
        
        List<Meal> meals = mealRepository.findByUserIdAndDate(userId, date);
        
        if (meals.isEmpty()) {
            return NutritionSummary.empty();
        }
        
        return nutritionService.calculateFromMeals(meals);
    }
    
    public NutritionGoal setDailyGoals(Long userId, double calories, 
                                      double protein, double carbs, double fats) {
        ValidationUtil.validateNotNull(userId, "userId");
        ValidationUtil.validatePositive(calories, "calories");
        ValidationUtil.validatePositive(protein, "protein");
        ValidationUtil.validatePositive(carbs, "carbs");
        ValidationUtil.validatePositive(fats, "fats");
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        NutritionGoal goal = new NutritionGoal(user, (int)calories, (int)protein, (int)carbs, (int)fats);
        user.setNutritionGoal(goal);
        userRepository.save(user);
        
        return goal;
    }
    
    public List<String> generateShoppingList(Long userId, int weekNumber) {
        ValidationUtil.validateNotNull(userId, "userId");
        ValidationUtil.validateMinValue(weekNumber, 1, "weekNumber");
        ValidationUtil.validateMaxValue(weekNumber, 52, "weekNumber");
        
        LocalDate startDate = LocalDate.now()
            .withDayOfYear((weekNumber - 1) * 7 + 1);
        LocalDate endDate = startDate.plusDays(7).minusDays(1);
        
        List<Meal> meals = mealRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        
        Set<String> ingredients = new HashSet<>();
        meals.forEach(meal -> 
            meal.getFoods().forEach(food -> 
                ingredients.addAll(food.getIngredients())
            )
        );
        
        return new ArrayList<>(ingredients);
    }
    
    public List<Food> suggestMeals(Long userId, Map<String, Object> preferences) {
        ValidationUtil.validateNotNull(userId, "userId");
        ValidationUtil.validateNotNull(preferences, "preferences");
        
        if (preferences.isEmpty()) {
            throw new InvalidInputException("Preferences map cannot be empty");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        return nutritionService.findSuitableMeals(user, preferences);
    }

    public void trackWaterIntake(Long userId, double amount, LocalDate date) {
        ValidationUtil.validatePositive(amount, "amount");
        ValidationUtil.validateNotNull(date, "date");
        ValidationUtil.validateNotNull(userId, "userId");
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Map<LocalDate, Double> waterIntake = user.getWaterIntake();
        double currentIntake = waterIntake.getOrDefault(date, 0.0);
        waterIntake.put(date, currentIntake + amount);
        userRepository.save(user);
    }


}