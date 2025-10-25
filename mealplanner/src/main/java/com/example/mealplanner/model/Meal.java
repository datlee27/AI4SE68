package com.example.mealplanner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "meal_time")
    private LocalDateTime mealTime;

    @Column(name = "date")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private MealType mealType;

    @ManyToMany
    @JoinTable(
        name = "meal_foods",
        joinColumns = @JoinColumn(name = "meal_id"),
        inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    private List<Food> foods = new ArrayList<>();

    public void addFood(Food food) {
        foods.add(food);
        food.getMeals().add(this);
    }

    public void removeFood(Food food) {
        foods.remove(food);
        food.getMeals().remove(this);
    }

    public double getTotalCalories() {
        return foods.stream().mapToDouble(Food::getCalories).sum();
    }

    public double getTotalProtein() {
        return foods.stream().mapToDouble(Food::getProtein).sum();
    }

    public double getTotalCarbs() {
        return foods.stream().mapToDouble(Food::getCarbs).sum();
    }

    public double getTotalFats() {
        return foods.stream().mapToDouble(Food::getFats).sum();
    }
}