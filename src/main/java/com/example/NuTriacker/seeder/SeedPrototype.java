package com.example.NuTriacker.seeder;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SeedPrototype {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Long logId;
    private String logDate;
    private BigDecimal dailyCalories;
    private BigDecimal dailyProteins;
    private BigDecimal dailyCarbs;
    private BigDecimal dailyFats;
    private Long mealId;
    private String mealName;
    private String mealTime;
    private BigDecimal mealCalories;
    private BigDecimal mealProteins;
    private BigDecimal mealCarbs;
    private BigDecimal mealFats;
    private Long itemId;
    private Long nutritionixFoodId;
    private String foodName;
    private BigDecimal itemCalories;
    private BigDecimal itemProteins;
    private BigDecimal itemCarbs;
    private BigDecimal itemFats;
}
