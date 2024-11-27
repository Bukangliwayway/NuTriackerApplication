package com.example.NuTriacker.controller;

import com.example.NuTriacker.exception.ResourceNotFoundException;
import com.example.NuTriacker.model.MealItem;
import com.example.NuTriacker.request.AddMealItemRequest;
import com.example.NuTriacker.response.ApiResponse;
import com.example.NuTriacker.service.MealItem.MealItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("${api.prefix}/menu-item")
@RequiredArgsConstructor
public class MealItemController {

    private final MealItemService mealItemService;

    @PostMapping
    public ResponseEntity<ApiResponse> addMenuItem(@RequestBody AddMealItemRequest request) {
        try {
            MealItem mealItem = mealItemService.addMealItem(request);
            return ResponseEntity.ok(new ApiResponse("Meal Item Added Successfully!", mealItem));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse> addMenuItems(@RequestBody List<AddMealItemRequest> requests) {
        try {
            List<MealItem> savedItems = mealItemService.addMealItems(requests);
            return ResponseEntity.ok(new ApiResponse("Meal Items Added Successfully!", savedItems));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
