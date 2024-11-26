package com.example.NuTriacker.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AddMealItemRequest {
    private String email;
    private LocalDate date;
    private String mealName;
    private LocalTime mealTime;
    private String foodName;
    private BigDecimal weight;
}
