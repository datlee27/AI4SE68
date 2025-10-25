package com.example.mealplanner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "foods")
@Data
@NoArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @PositiveOrZero(message = "Calories must be non-negative")
    private double calories;

    @PositiveOrZero(message = "Protein must be non-negative")
    private double protein;

    @PositiveOrZero(message = "Carbs must be non-negative")
    private double carbs;

    @PositiveOrZero(message = "Fats must be non-negative")
    private double fats;

    public void setCalories(double calories) {
        if (calories < 0) {
            throw new IllegalArgumentException("Calories cannot be negative");
        }
        this.calories = calories;
    }

    public void setProtein(double protein) {
        if (protein < 0) {
            throw new IllegalArgumentException("Protein cannot be negative");
        }
        this.protein = protein;
    }

    public void setCarbs(double carbs) {
        if (carbs < 0) {
            throw new IllegalArgumentException("Carbs cannot be negative");
        }
        this.carbs = carbs;
    }

    public void setFats(double fats) {
        if (fats < 0) {
            throw new IllegalArgumentException("Fats cannot be negative");
        }
        this.fats = fats;
    }
    
    @ElementCollection
    @CollectionTable(
        name = "food_ingredients",
        joinColumns = @JoinColumn(name = "food_id")
    )
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();
    
    @ManyToMany(mappedBy = "foods")
    private List<Meal> meals = new ArrayList<>();
}