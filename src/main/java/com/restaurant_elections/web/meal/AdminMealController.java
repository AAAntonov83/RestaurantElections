package com.restaurant_elections.web.meal;

import com.restaurant_elections.model.Meal;
import com.restaurant_elections.repository.MealRepository;
import com.restaurant_elections.web.AuthUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.restaurant_elections.util.validation.ValidationUtil.assureIdConsistent;
import static com.restaurant_elections.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(AdminMealController.REST_URL)
@Slf4j
@AllArgsConstructor
public class AdminMealController {
    static final String REST_URL = "/api/admin/meals";
    private final MealRepository mealRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Meal> create(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody Meal meal) {
        log.info("Meal {} created by {}", meal, authUser);
        checkNew(meal);
        Meal created = mealRepository.save(meal);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(AdminMealController.REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id, @Valid @RequestBody Meal updated) {
        log.info("Meal {} update by {}", updated, authUser);
        assureIdConsistent(updated, id);
        Meal meal = mealRepository.getExisted(id);
        meal.setName(updated.getName());
        meal.setPrice(updated.getPrice());
        mealRepository.save(meal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        mealRepository.deleteExisted(id);
        log.info("Meal {} deleted by {}", id, authUser);
    }
}
