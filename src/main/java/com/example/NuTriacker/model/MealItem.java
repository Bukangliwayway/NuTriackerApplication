package com.example.NuTriacker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MealItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long nutritionixFoodId;
    private String foodName;
    private BigDecimal calories;
    private BigDecimal proteins;
    private BigDecimal carbs;
    private BigDecimal fats;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "meal_id")
    private Meal meal;

    public MealItem(String foodName, BigDecimal weight, BigDecimal calories, BigDecimal proteins, BigDecimal carbs, BigDecimal fats, Meal meal) {
    }
}
