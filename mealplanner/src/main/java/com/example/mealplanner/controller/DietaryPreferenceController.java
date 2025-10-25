package com.example.mealplanner.controller;

import com.example.mealplanner.service.MealPlannerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dietary-preferences")
public class DietaryPreferenceController {

    private final MealPlannerService mealPlannerService;

    public DietaryPreferenceController(MealPlannerService mealPlannerService) {
        this.mealPlannerService = mealPlannerService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> updatePreferences(@PathVariable Long userId,
                                             @RequestBody java.util.Map<String, String> preferences) {
        try {
            mealPlannerService.updateDietaryPreferences(userId, preferences);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}