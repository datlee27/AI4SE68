package com.example.mealplanner.controller;

import com.example.mealplanner.model.Food;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;
import com.example.mealplanner.service.MealPlannerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/meals")
public class MealController {

    private final MealPlannerService mealPlannerService;

    public MealController(MealPlannerService mealPlannerService) {
        this.mealPlannerService = mealPlannerService;
    }

    @GetMapping("/add")
    public String showAddMealForm(Model model) {
        model.addAttribute("today", LocalDateTime.now());
        model.addAttribute("mealTypes", MealType.values());
        return "meal-form";
    }

    @PostMapping("/add")
    public String addMeal(@RequestParam Long userId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                         @RequestParam MealType mealType,
                         @RequestParam List<Long> foods) {
        try {
            if (userId == null || date == null || mealType == null || foods == null) {
                throw new IllegalArgumentException("Missing required parameters");
            }
            
            // Validate food IDs
            for (Long foodId : foods) {
                if (foodId == null) {
                    throw new IllegalArgumentException("Invalid food ID");
                }
            }
            
            mealPlannerService.addMeal(userId, date, mealType, foods);
            return "redirect:/meals";
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding meal");
        }
    }

    @GetMapping
    public String listMeals(Model model) {
        return "meals";
    }
}