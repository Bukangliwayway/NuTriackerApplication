package com.example.NuTriacker.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class NutritionixAppResponse {
    private List<FoodItem> foods;

    @Data
    public static class FoodItem {
        private String food_name;
        private BigDecimal serving_weight_grams;
        private BigDecimal nf_calories;
        private BigDecimal nf_protein;
        private BigDecimal nf_total_carbohydrate;
        private BigDecimal nf_total_fat;
    }
}