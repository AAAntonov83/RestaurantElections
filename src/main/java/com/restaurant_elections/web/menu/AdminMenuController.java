package com.restaurant_elections.web.menu;

import com.restaurant_elections.error.NotFoundException;
import com.restaurant_elections.model.Dish;
import com.restaurant_elections.model.Menu;
import com.restaurant_elections.repository.DishRepository;
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
    private final DishRepository dishRepository;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> get(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Menu {} requested by {}", id, authUser);
        return ResponseEntity.of(menuRepository.findById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Menu {} delete by {}", id, authUser);
        menuRepository.deleteExisted(id);
    }

    @PostMapping(value = "/{menuId}/include-dish/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void includeDish(@AuthenticationPrincipal AuthUser authUser, @PathVariable int menuId, @PathVariable int dishId) {
        log.info("Dish id={} include to Menu id={} by {}", dishId, menuId, authUser);
        Menu menu = menuRepository.getByIdWithDishes(menuId).orElseThrow(() -> new NotFoundException("Menu with id=" + menuId + " not found"));
        Dish dish = dishRepository.getExisted(dishId);
        menu.addDish(dish);
        menuRepository.save(menu);
    }

    @PostMapping("/{menuId}/exclude-dish/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excludeDish(@AuthenticationPrincipal AuthUser authUser, @PathVariable int menuId, @PathVariable int dishId) {
        log.info("Dish {} exclude from Menu {} by {}", dishId, menuId, authUser);
        Menu menu = menuRepository.getByIdWithDishes(menuId).orElseThrow(() -> new NotFoundException("Menu with id=" + menuId + " not found"));
        menu.getDishes().removeIf(dish -> dish.id() == dishId);
        menuRepository.save(menu);
    }
}
