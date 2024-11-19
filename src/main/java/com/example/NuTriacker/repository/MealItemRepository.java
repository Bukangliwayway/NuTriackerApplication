package com.example.NuTriacker.repository;

import com.example.NuTriacker.model.MealItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealItemRepository extends JpaRepository<MealItem, Integer> {

}
