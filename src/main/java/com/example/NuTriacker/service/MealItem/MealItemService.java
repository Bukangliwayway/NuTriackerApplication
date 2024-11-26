package com.example.NuTriacker.service.MealItem;

import com.example.NuTriacker.model.DailyLog;
import com.example.NuTriacker.model.Meal;
import com.example.NuTriacker.model.MealItem;
import com.example.NuTriacker.model.User;
import com.example.NuTriacker.repository.DailyLogRepository;
import com.example.NuTriacker.repository.MealItemRepository;
import com.example.NuTriacker.repository.MealRepository;
import com.example.NuTriacker.repository.UserRepository;
import com.example.NuTriacker.request.AddMealItemRequest;
import com.example.NuTriacker.response.NutritionixAppResponse;
import com.example.NuTriacker.service.Nutritionix.NutritionixService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealItemService implements  IMealItemService{
    private final UserRepository userRepo;
    private final DailyLogRepository dailyLogRepo;
    private final MealRepository mealRepo;
    private final MealItemRepository mealItemRepo;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private NutritionixService nutritionixService;

    @Override
    public MealItem addMealItem(AddMealItemRequest request) {
        User user;
        DailyLog dailyLog;
        Meal meal;

        //Create a random password for the user
        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);

        Optional<User> userExists = userRepo.findByEmail(request.getEmail());
        user = userExists.orElseGet(() -> userRepo.save(new User(encodedPassword, request.getEmail())));

        Optional<DailyLog> dailyLogExists = dailyLogRepo.findByDateAndUser(request.getDate(), user);
        dailyLog = dailyLogExists.orElseGet(() -> dailyLogRepo.save(new DailyLog(request.getDate(), user)));

        Optional<Meal> mealExists = mealRepo.findByMealNameAndMealTimeAndDailyLog(request.getMealName(), request.getMealTime(), dailyLog);
        meal = mealExists.orElseGet(() -> mealRepo.save(new Meal(request.getMealName(), request.getMealTime(), dailyLog)));

        NutritionixAppResponse.FoodItem foodData = nutritionixService.getFoodData(request.getFoodName());

        // Calculate nutrients per gram
        BigDecimal caloriesPerGram = foodData.getNf_calories()
                .divide(foodData.getServing_weight_grams(), 6, RoundingMode.HALF_UP);
        BigDecimal proteinPerGram = foodData.getNf_protein()
                .divide(foodData.getServing_weight_grams(), 6, RoundingMode.HALF_UP);
        BigDecimal carbsPerGram = foodData.getNf_total_carbohydrate()
                .divide(foodData.getServing_weight_grams(), 6, RoundingMode.HALF_UP);
        BigDecimal fatPerGram = foodData.getNf_total_fat()
                .divide(foodData.getServing_weight_grams(), 6, RoundingMode.HALF_UP);

        // Calculate total nutrients based on weight
        BigDecimal totalCalories = caloriesPerGram.multiply(request.getWeight());
        BigDecimal totalProtein = proteinPerGram.multiply(request.getWeight());
        BigDecimal totalCarbs = carbsPerGram.multiply(request.getWeight());
        BigDecimal totalFat = fatPerGram.multiply(request.getWeight());

        MealItem mealItem = new MealItem(
                request.getFoodName(),
                request.getWeight(),
                totalCalories,
                totalProtein,
                totalCarbs,
                totalFat,
                meal
        );

        return null;
    }

}
