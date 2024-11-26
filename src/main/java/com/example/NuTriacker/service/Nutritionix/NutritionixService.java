package com.example.NuTriacker.service.Nutritionix;

import com.example.NuTriacker.exception.FoodNotFoundException;
import com.example.NuTriacker.response.NutritionixAppResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class NutritionixService {

    @Value("${nutritionix.api.id}")
    private String apiId;

    @Value("${nutritionix.api.key}")
    private String apiKey;

    @Value("${nutritionix.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public NutritionixService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NutritionixAppResponse.FoodItem getFoodData(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-app-id", apiId);
        headers.set("x-app-key", apiKey);

        Map<String, String> body = new HashMap<>();
        body.put("query", query);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        NutritionixAppResponse response = restTemplate.postForObject(
                apiUrl,
                request,
                NutritionixAppResponse.class
        );

        Optional.ofNullable(response.getFoods())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new FoodNotFoundException("No food items found"));

        return response.getFoods()
                .stream()
                .findFirst()
                .orElseThrow(() -> new FoodNotFoundException("No food items found"));
    }
}