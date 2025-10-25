package com.example.mealplanner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    
    @Email(message = "Invalid email format")
    @NotEmpty(message = "Email cannot be empty")
    @Column(unique = true)
    private String email;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private NutritionGoal nutritionGoal;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<DietaryPreference> dietaryPreferences = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Meal> meals = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "water_intake",
        joinColumns = @JoinColumn(name = "user_id")
    )
    private Map<LocalDate, Double> waterIntake = new HashMap<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void addDietaryPreference(String type, String value) {
        DietaryPreference preference = new DietaryPreference(this, type, value);
        dietaryPreferences.add(preference);
    }
}