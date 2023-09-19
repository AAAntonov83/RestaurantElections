package com.restaurant_elections.web.restaurant;

import com.restaurant_elections.model.Menu;
import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.repository.MenuRepository;
import com.restaurant_elections.repository.RestaurantRepository;
import com.restaurant_elections.util.DateTimeUtil;
import com.restaurant_elections.web.AuthUser;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.Optional;

@RestController
@RequestMapping(RestaurantController.REST_URL)
@Slf4j
@AllArgsConstructor
@Tag(name = "restaurant controller")
public class RestaurantController {
    public static final String REST_URL = "/api/restaurants";
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    @GetMapping("/with-menu")

    public List<Restaurant> getAllWithMenuOnTodayOnly(@AuthenticationPrincipal AuthUser authUser) {
        log.info("find all restaurants with current menu for {}", authUser);
        return restaurantRepository.findAllWithMenuOnDate(DateTimeUtil.currentDate());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> get(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("get restaurant {} for {}", id, authUser);
        Optional<Restaurant> restaurant = Optional.of(restaurantRepository.getExisted(id));
        return ResponseEntity.of(restaurant);
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<Menu> getMenuOnToday(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("get current restaurant {} menu for {}", id, authUser);
        Optional<Menu> menu = menuRepository.getByRestaurantIdWithDishesOnDate(id, DateTimeUtil.currentDate());
        return ResponseEntity.of(menu);
    }

}
