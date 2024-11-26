package com.example.NuTriacker.repository;

import com.example.NuTriacker.model.DailyLog;
import com.example.NuTriacker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Integer> {
    Optional<DailyLog> findByDateAndUser(LocalDate date, User user);

}
