package com.example.NuTriacker.processor;

import com.example.NuTriacker.model.DailyLog;
import com.example.NuTriacker.seeder.SeedPrototype;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;


@Component
public class DailyLogProcessor implements ItemProcessor<SeedPrototype, DailyLog>{
    @Override
    public DailyLog process(SeedPrototype item) throws Exception {
        DailyLog dailyLog = new DailyLog();
        dailyLog.setId(item.getLogId());
        dailyLog.setDate(LocalDate.parse(item.getLogDate()));
        dailyLog.setTotalDailyCalories(item.getMealCalories());
        dailyLog.setTotalDailyProteins(item.getMealProteins());
        dailyLog.setTotalDailyCarbs(item.getMealCarbs());
        dailyLog.setTotalDailyFats(item.getMealFats());
        return dailyLog;
    }
}
