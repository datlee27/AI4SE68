package com.example.mealplanner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nutrition_goals")
@Data
@NoArgsConstructor
public class NutritionGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Min(value = 0, message = "Calories target must be non-negative")
    @Column(name = "calories")
    private int calories;
    
    @Min(value = 0, message = "Protein target must be non-negative")
    @Column(name = "protein")
    private int protein;
    
    @Min(value = 0, message = "Carbs target must be non-negative")
    @Column(name = "carbs")
    private int carbs;
    
    @Min(value = 0, message = "Fats target must be non-negative")
    @Column(name = "fats")
    private int fats;
    
    public NutritionGoal(User user, int calories, int protein, int carbs, int fats) {
        this.user = user;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

    public NutritionGoal(int calories, int protein, int carbs, int fats) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }
}