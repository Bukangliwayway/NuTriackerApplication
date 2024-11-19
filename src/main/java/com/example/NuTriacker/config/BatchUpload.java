package com.example.NuTriacker.config;

import com.example.NuTriacker.model.DailyLog;
import com.example.NuTriacker.model.Meal;
import com.example.NuTriacker.model.MealItem;
import com.example.NuTriacker.model.User;
import com.example.NuTriacker.repository.DailyLogRepository;
import com.example.NuTriacker.repository.MealItemRepository;
import com.example.NuTriacker.repository.MealRepository;
import com.example.NuTriacker.repository.UserRepository;
import com.example.NuTriacker.seeder.SeedPrototype;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchUpload {
    // Constants
    private static final String CSV_FILE_PATH = "src/main/java/seeder/seed.csv";
    private static final String[] CSV_COLUMN_NAMES = {
            "user_id", "first_name", "last_name", "email", "password",
            "log_id", "log_date", "daily_calories", "daily_proteins", "daily_carbs",
            "daily_fats", "meal_id", "meal_name", "meal_time", "meal_calories",
            "meal_proteins", "meal_carbs", "meal_fats", "item_id", "nutritionix_food_id",
            "food_name", "item_calories", "item_proteins", "item_carbs", "item_fats"
    };

    // Dependencies
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final UserRepository userRepo;
    private final DailyLogRepository dailyLogRepo;
    private final MealRepository mealRepo;
    private final MealItemRepository mealItemRepo;


    // Task Executor Configuration
    @Bean
    public TaskExecutor asyncTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(1);
        return executor;
    }


    //Writer
    public ItemWriter<User> userWriter() {
        return userRepo::saveAll;
    }
    public ItemWriter<Meal> mealWriter() { return mealRepo::saveAll; }
    public ItemWriter<DailyLog> dailyLogWriter() { return dailyLogRepo::saveAll;}
    public ItemWriter<MealItem> mealItemWriter() { return mealItemRepo::saveAll;}


    // Helper Methods
    private LineMapper<SeedPrototype> createLineMapper() {
        DefaultLineMapper<SeedPrototype> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames(CSV_COLUMN_NAMES);

        BeanWrapperFieldSetMapper<SeedPrototype> fieldMapper = new BeanWrapperFieldSetMapper<>();
        fieldMapper.setTargetType(SeedPrototype.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldMapper);

        return lineMapper;
    }


}
