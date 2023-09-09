package com.restaurant_elections.web.meal;

import com.restaurant_elections.model.Meal;
import com.restaurant_elections.repository.MealRepository;
import com.restaurant_elections.web.AuthUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(MealController.REST_URL)
@Slf4j
@AllArgsConstructor
public class MealController {
    static final String REST_URL = "/api/meals";
    private final MealRepository mealRepository;

    @GetMapping
    public List<Meal> get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("All meals requested by {}", authUser);
        return mealRepository.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Meal> get(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Meal id={} requested by {}", id, authUser);
        return ResponseEntity.of(mealRepository.findById(id));
    }
}
