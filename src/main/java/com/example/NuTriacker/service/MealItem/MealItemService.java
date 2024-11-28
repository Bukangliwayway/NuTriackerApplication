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
import jakarta.transaction.Transactional;
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
import java.util.*;

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

    @Transactional
    @Override
    public MealItem addMealItem(AddMealItemRequest request) {
        User user;
        DailyLog dailyLog;
        Meal meal;

        //Create a random password for the user
        String randomPassword = UUID.randomUUID().toString();

        Optional<User> userExists = userRepo.findByEmail(request.getEmail());
        user = userExists.orElseGet(() -> userRepo.save(new User(randomPassword, request.getEmail())));

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

        mealItemRepo.save(mealItem);

        //Update the meal and daily log content
        updateMealContent(meal);
        try{
            updateDailyLogContent(dailyLog);
            System.out.println(dailyLog.getDate());
        }catch (Exception e){
            System.out.println(e);
        }
        return mealItem;
    }

    @Transactional
    public List<MealItem> addMealItems(List<AddMealItemRequest> requests) {
        Map<String, User> userCache = new HashMap<>();
        Map<LocalDate, Map<String, DailyLog>> dailyLogCache = new HashMap<>();
        Map<String, Meal> mealCache = new HashMap<>();
        List<MealItem> mealItems = new ArrayList<>();
        Set<Meal> mealsToUpdate = new HashSet<>();
        Set<DailyLog> logsToUpdate = new HashSet<>();

        for (AddMealItemRequest request : requests) {
            // Get or create user
            User user = userCache.computeIfAbsent(request.getEmail(), email -> {
                Optional<User> existing = userRepo.findByEmail(email);
                return existing.orElseGet(() -> userRepo.save(new User(UUID.randomUUID().toString(), email)));
            });

            // Get or create daily log
            DailyLog dailyLog = dailyLogCache
                    .computeIfAbsent(request.getDate(), date -> new HashMap<>())
                    .computeIfAbsent(user.getId().toString(), userId -> {
                        Optional<DailyLog> existing = dailyLogRepo.findByDateAndUser(request.getDate(), user);
                        return existing.orElseGet(() -> dailyLogRepo.save(new DailyLog(request.getDate(), user)));
                    });

            // Get or create meal
            String mealKey = String.format("%s_%s_%s",
                    dailyLog.getId(), request.getMealName(), request.getMealTime());
            Meal meal = mealCache.computeIfAbsent(mealKey, k -> {
                Optional<Meal> existing = mealRepo.findByMealNameAndMealTimeAndDailyLog(
                        request.getMealName(), request.getMealTime(), dailyLog);
                return existing.orElseGet(() -> mealRepo.save(new Meal(request.getMealName(), request.getMealTime(), dailyLog)));
            });

            // Get nutrition data and calculate
            NutritionixAppResponse.FoodItem foodData = nutritionixService.getFoodData(request.getFoodName());

            MealItem mealItem = getMealItem(request, foodData, meal);

            mealItems.add(mealItem);
            mealsToUpdate.add(meal);
            logsToUpdate.add(dailyLog);
        }

        // Batch save meal items
        List<MealItem> savedItems = mealItemRepo.saveAll(mealItems);
        mealItemRepo.flush();

        // Batch update meals and daily logs
        mealsToUpdate.forEach(meal -> {
            updateMealContent(meal);
            mealRepo.flush();
        });

        logsToUpdate.forEach(log -> {
            updateDailyLogContent(log);
            dailyLogRepo.flush();
        });

        return savedItems;
    }

    private MealItem getMealItem(AddMealItemRequest request, NutritionixAppResponse.FoodItem foodData, Meal meal) {
        BigDecimal weight = request.getWeight();
        BigDecimal servingWeight = foodData.getServing_weight_grams();

        MealItem mealItem = new MealItem(
                request.getFoodName(),
                weight,
                calculatePerGram(foodData.getNf_calories(), servingWeight).multiply(weight),
                calculatePerGram(foodData.getNf_protein(), servingWeight).multiply(weight),
                calculatePerGram(foodData.getNf_total_carbohydrate(), servingWeight).multiply(weight),
                calculatePerGram(foodData.getNf_total_fat(), servingWeight).multiply(weight),
                meal
        );

        // Add to meal's collection
        if (meal.getMealItems() == null) {
            meal.setMealItems(new ArrayList<>());
        }
        meal.getMealItems().add(mealItem);
        return mealItem;
    }

    private BigDecimal calculatePerGram(BigDecimal nutrient, BigDecimal servingWeight) {
        return nutrient.divide(servingWeight, 6, RoundingMode.HALF_UP);
    }

    @Transactional
    public void updateMealContent(Meal meal){
        BigDecimal totalCalories = BigDecimal.valueOf(0);
        BigDecimal totalProteins = BigDecimal.valueOf(0);
        BigDecimal totalCarbs = BigDecimal.valueOf(0);
        BigDecimal totalFats = BigDecimal.valueOf(0);
        if (meal.getMealItems() != null) {
            for (MealItem mealItem : meal.getMealItems()) {
                totalCalories = totalCalories.add(mealItem.getCalories());
                totalProteins = totalProteins.add(mealItem.getProteins());
                totalCarbs = totalCarbs.add(mealItem.getCarbs());
                totalFats = totalFats.add(mealItem.getFats());
            }
        }
        meal.setTotalCalories(totalCalories);
        meal.setTotalProteins(totalProteins);
        meal.setTotalCarbs(totalCarbs);
        meal.setTotalFats(totalFats);
        mealRepo.saveAndFlush(meal);
    }

    @Transactional
    public void updateDailyLogContent(DailyLog dailyLog){

        BigDecimal totalCalories = BigDecimal.valueOf(0);
        BigDecimal totalProteins = BigDecimal.valueOf(0);
        BigDecimal totalCarbs = BigDecimal.valueOf(0);
        BigDecimal totalFats = BigDecimal.valueOf(0);

        if(dailyLog.getMeals() != null){
            for(Meal meal : dailyLog.getMeals()){
                totalCalories = totalCalories.add(meal.getTotalCalories());
                totalProteins = totalProteins.add(meal.getTotalProteins());
                totalCarbs = totalCarbs.add(meal.getTotalCarbs());
                totalFats = totalFats.add(meal.getTotalFats());
            }
        }

        dailyLog.setTotalDailyCalories(totalCalories);
        dailyLog.setTotalDailyProteins(totalProteins);
        dailyLog.setTotalDailyCarbs(totalCarbs);
        dailyLog.setTotalDailyFats(totalFats);
        dailyLogRepo.saveAndFlush(dailyLog);
    }

}
