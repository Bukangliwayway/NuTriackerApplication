package com.example.NuTriacker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DailyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private BigDecimal totalDailyCalories;
    private BigDecimal totalDailyProteins;
    private BigDecimal totalDailyCarbs;
    private BigDecimal totalDailyFats;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Meal> meals;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public DailyLog(LocalDate date, User user) {
        this.date = date;
        this.totalDailyCalories = BigDecimal.valueOf(0);
        this.totalDailyProteins = BigDecimal.valueOf(0);
        this.totalDailyCarbs = BigDecimal.valueOf(0);
        this.totalDailyFats = BigDecimal.valueOf(0);
        this.user = user;
        this.meals = new ArrayList<>();
    }

    public void recomputeTotalNutrients() {
        this.totalDailyCalories = BigDecimal.valueOf(0);
        this.totalDailyProteins = BigDecimal.valueOf(0);
        this.totalDailyCarbs = BigDecimal.valueOf(0);
        this.totalDailyFats = BigDecimal.valueOf(0);
        for (Meal meal : meals) {
            this.totalDailyCalories = this.totalDailyCalories.add(meal.getTotalCalories());
            this.totalDailyProteins = this.totalDailyProteins.add(meal.getTotalProteins());
            this.totalDailyCarbs = this.totalDailyCarbs.add(meal.getTotalCarbs());
            this.totalDailyFats = this.totalDailyFats.add(meal.getTotalFats());
        }
    }
}
