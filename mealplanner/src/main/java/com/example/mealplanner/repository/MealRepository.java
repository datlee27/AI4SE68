package com.example.mealplanner.repository;

import com.example.mealplanner.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByUserIdAndMealTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    List<Meal> findByUserIdAndDate(Long userId, LocalDate date);
    List<Meal> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}