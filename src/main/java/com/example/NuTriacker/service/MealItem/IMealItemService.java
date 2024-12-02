package com.example.NuTriacker.service.MealItem;

import com.example.NuTriacker.model.MealItem;
import com.example.NuTriacker.request.AddMealItemRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IMealItemService {
    MealItem addMealItem(AddMealItemRequest request);
    List<MealItem> addMealItems(List<AddMealItemRequest> requests);
}
