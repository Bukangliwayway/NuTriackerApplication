package com.example.NuTriacker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.batch.core.JobExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/upload-csv")
@RequiredArgsConstructor
@Slf4j
public class UploadController {
    private final JobLauncher jobLauncher;
    private final Job job;

    @PostMapping
    public ResponseEntity<String> csvImportJob() {
        log.info("CSV import job triggered manually");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startsAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            JobExecution execution = jobLauncher.run(job, jobParameters);
            return ResponseEntity.ok("Job started with status: " + execution.getStatus());
        } catch (Exception e) {
            log.error("Job failed to start", e);
            return ResponseEntity.internalServerError().body("Job failed: " + e.getMessage());
        }
    }
}