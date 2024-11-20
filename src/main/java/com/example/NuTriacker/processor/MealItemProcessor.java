package com.example.NuTriacker.processor;

import com.example.NuTriacker.model.MealItem;
import com.example.NuTriacker.seeder.SeedPrototype;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class MealItemProcessor implements ItemProcessor<SeedPrototype, MealItem>{
    @Override
    public MealItem process(SeedPrototype item) throws Exception {
        MealItem mealItem = new MealItem();
        mealItem.setId(item.getItemId());
        mealItem.setFoodName(item.getFoodName());
        mealItem.setNutritionixFoodId(item.getNutritionixFoodId());
        mealItem.setCalories(item.getMealCalories());
        mealItem.setProteins(item.getMealProteins());
        mealItem.setCarbs(item.getMealCarbs());
        mealItem.setFats(item.getMealFats());
        return mealItem;
    }
}
