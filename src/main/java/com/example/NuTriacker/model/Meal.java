package com.example.NuTriacker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mealName;
    private LocalTime mealTime;
    private BigDecimal totalCalories;
    private BigDecimal totalProteins;
    private BigDecimal totalCarbs;
    private BigDecimal totalFats;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealItem> mealItems;

    @ManyToOne
    @JoinColumn(name = "daily_log_id")
    private DailyLog dailyLog;

    public Meal(String mealName, LocalTime mealTime, DailyLog dailyLog) {
        this.mealName = mealName;
        this.mealTime = mealTime;
        this.totalCalories = BigDecimal.valueOf(0);
        this.totalProteins = BigDecimal.valueOf(0);
        this.totalCarbs = BigDecimal.valueOf(0);
        this.totalFats = BigDecimal.valueOf(0);
        this.dailyLog = dailyLog;
    }
}
