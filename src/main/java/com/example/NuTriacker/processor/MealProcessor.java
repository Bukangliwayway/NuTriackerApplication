package com.example.NuTriacker.processor;

import com.example.NuTriacker.model.Meal;
import com.example.NuTriacker.seeder.SeedPrototype;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;


@Component
public class MealProcessor implements ItemProcessor<SeedPrototype, Meal>{
    @Override
    public Meal process(SeedPrototype item) throws Exception {
        Meal meal = new Meal();
        meal.setId(item.getMealId());
        meal.setMealTime(LocalTime.parse(item.getMealTime()));
        meal.setTotalCalories(item.getMealCalories());
        meal.setTotalProteins(item.getMealProteins());
        meal.setTotalCarbs(item.getMealCarbs());
        meal.setTotalFats(item.getMealFats());
        return meal;
    }
}
