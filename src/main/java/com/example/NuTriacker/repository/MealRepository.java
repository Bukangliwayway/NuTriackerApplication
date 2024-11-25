package com.example.NuTriacker.repository;

import com.example.NuTriacker.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {

}
