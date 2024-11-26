package com.example.NuTriacker.repository;

import com.example.NuTriacker.model.DailyLog;
import com.example.NuTriacker.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {
    Optional<Meal> findByMealNameAndMealTimeAndDailyLog(String mealName, LocalTime mealTime, DailyLog dailyLog);

}
