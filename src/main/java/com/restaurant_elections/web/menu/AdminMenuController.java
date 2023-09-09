package com.restaurant_elections.web.menu;

import com.restaurant_elections.error.NotFoundException;
import com.restaurant_elections.model.Meal;
import com.restaurant_elections.model.Menu;
import com.restaurant_elections.repository.MealRepository;
import com.restaurant_elections.repository.MenuRepository;
import com.restaurant_elections.web.AuthUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AdminMenuController.REST_URL)
@Slf4j
@AllArgsConstructor
public class AdminMenuController {
    static final String REST_URL = "/api/admin/menus";
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> get(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Menu {} requested by {}", id, authUser);
        return ResponseEntity.of(menuRepository.getByIdWithMeals(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Menu {} delete by {}", id, authUser);
        menuRepository.deleteExisted(id);
    }

    @PostMapping(value = "/{menuId}/include-meal/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void includeMeal(@AuthenticationPrincipal AuthUser authUser, @PathVariable int menuId, @PathVariable int mealId) {
        log.info("Meal id={} include to Menu id={} by {}", mealId, menuId, authUser);
        Menu menu = menuRepository.getByIdWithMeals(menuId).orElseThrow(() -> new NotFoundException("Menu with id=" + menuId + " not found"));
        Meal meal = mealRepository.getExisted(mealId);
        menu.addMeal(meal);
        menuRepository.save(menu);
    }

    @PostMapping("/{menuId}/exclude-meal/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excludeMeal(@AuthenticationPrincipal AuthUser authUser, @PathVariable int menuId, @PathVariable int mealId) {
        log.info("Meal {} exclude from Menu {} by {}", mealId, menuId, authUser);
        Menu menu = menuRepository.getByIdWithMeals(menuId).orElseThrow(() -> new NotFoundException("Menu with id=" + menuId + " not found"));
        menu.getMeals().removeIf(meal -> meal.id() == mealId);
        menuRepository.save(menu);
    }
}
