package com.example.mealplanner.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dietary_preferences")
@Data
@NoArgsConstructor
public class DietaryPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "preference_type")
    private String type;

    @Column(name = "preference_value")
    private String value;

    public DietaryPreference(User user, String type, String value) {
        this.user = user;
        this.type = type;
        this.value = value;
    }
}