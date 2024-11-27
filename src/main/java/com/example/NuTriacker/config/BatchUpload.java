package com.example.NuTriacker.config;

import com.example.NuTriacker.model.DailyLog;
import com.example.NuTriacker.model.Meal;
import com.example.NuTriacker.model.MealItem;
import com.example.NuTriacker.model.User;
import com.example.NuTriacker.processor.DailyLogProcessor;
import com.example.NuTriacker.processor.MealItemProcessor;
import com.example.NuTriacker.processor.MealProcessor;
import com.example.NuTriacker.processor.UserProcessor;
import com.example.NuTriacker.repository.DailyLogRepository;
import com.example.NuTriacker.repository.MealItemRepository;
import com.example.NuTriacker.repository.MealRepository;
import com.example.NuTriacker.repository.UserRepository;
import com.example.NuTriacker.seeder.SeedPrototype;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchUpload {
    // Constants
    private static final String CSV_FILE_PATH = "src/main/java/com/example/NuTriacker/seeder/seed.csv";
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

    //Caches
    private final Map<String, User> userCache = new HashMap<>();
    private final Map<String, DailyLog> dailyLogCache = new HashMap<>();
    private final Map<String, Meal> mealCache = new HashMap<>();

    //Repositories
    private final UserRepository userRepo;
    private final DailyLogRepository dailyLogRepo;
    private final MealRepository mealRepo;
    private final MealItemRepository mealItemRepo;

    //Processors
    private final UserProcessor userProcessor;
    private final DailyLogProcessor dailyLogProcessor;
    private final MealProcessor mealProcessor;
    private final MealItemProcessor mealItemProcessor;

    // Job Configuration
    @Bean
    public Job importStoresJob() {
        return new JobBuilder("importStores", jobRepository)
                .start(importStep())
                .build();
    }

    //Step Configuration
    @Bean
    public Step importStep() {
        return new StepBuilder("importCSV", jobRepository)
                .<SeedPrototype, MealItem>chunk(10, platformTransactionManager)
                .reader(csvReader())
                .processor(dataProcessor())
                .writer(mealItemWriter())
                .taskExecutor(asyncTaskExecutor())
                .listener(processListener())
                .build();
    }

    // Task Executor Configuration
    @Bean
    public TaskExecutor asyncTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(1);
        return executor;
    }


    // Reader Configuration
    @Bean
    public FlatFileItemReader<SeedPrototype> csvReader() {
        FlatFileItemReader<SeedPrototype> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(CSV_FILE_PATH));
        reader.setName("csv_import");
        reader.setLinesToSkip(1);
        reader.setLineMapper(createLineMapper());
        return reader;
    }

    // Processor Configuration
    @Bean
    public ItemProcessor<SeedPrototype, MealItem> dataProcessor () {
        return item -> {
            if (userCache.size() > 1000) {
                userCache.clear();
                dailyLogCache.clear();
                mealCache.clear();
            }

            if (item == null) {
                throw new IllegalArgumentException("Input item cannot be null");
            }

            // Get or create User
            User user = userCache.computeIfAbsent(item.getEmail(), email -> {
                Optional<User> existingUser = userRepo.findByEmail(email);
                if (existingUser.isPresent()) {
                    return existingUser.get();
                }

                User newUser;

                try {
                    newUser = userProcessor.process(item);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                assert newUser != null;
                return userRepo.save(newUser);
            });

            // Get or create DailyLog
            String dailyLogKey = user.getId() + "_" + item.getLogId() + "_" + item.getLogDate();
            DailyLog dailyLog = dailyLogCache.computeIfAbsent(dailyLogKey, key -> {
                DailyLog newDailyLog;
                try {
                    newDailyLog = dailyLogProcessor.process(item);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                assert newDailyLog != null;
                newDailyLog.setUser(user);
                return dailyLogRepo.save(newDailyLog);
            });

            // Get or create Meal
            String mealKey = dailyLog.getId() + "_" + item.getMealId();
            Meal meal = mealCache.computeIfAbsent(mealKey, key -> {
                Meal newMeal;
                try {
                    newMeal = mealProcessor.process(item);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                newMeal.setDailyLog(dailyLog);
                return mealRepo.save(newMeal);
            });

            MealItem newMealItem = new MealItem();
            try {
                newMealItem = mealItemProcessor.process(item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            assert newMealItem != null;
            newMealItem.setMeal(meal);
            return newMealItem;
        };
    }

    //Writer Configuration
    @Bean
    public ItemWriter<MealItem> mealItemWriter() { return mealItemRepo::saveAll;}

    //Listener Configuration
    @Bean
    public ItemProcessListener<SeedPrototype, MealItem> processListener() {
        return new ItemProcessListener<>() {
            @Override
            public void beforeProcess(SeedPrototype item) {
                log.debug("Processing item: {}", item);
            }

            @Override
            public void afterProcess(SeedPrototype item, MealItem result) {
                log.debug("Processed item: {}", result);
            }

            @Override
            public void onProcessError(SeedPrototype item, Exception e) {
                log.error("Error processing item: {}", item, e);
            }
        };
    }

    // LineMapper Configuration
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
