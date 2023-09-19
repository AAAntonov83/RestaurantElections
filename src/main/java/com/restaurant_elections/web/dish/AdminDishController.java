package com.restaurant_elections.web.dish;

import com.restaurant_elections.error.NotFoundException;
import com.restaurant_elections.model.Dish;
import com.restaurant_elections.repository.DishRepository;
import com.restaurant_elections.web.AuthUser;
import com.restaurant_elections.web.restaurant.AdminRestaurantController;
import jakarta.transaction.Transactional;
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
import java.util.List;

import static com.restaurant_elections.util.validation.ValidationUtil.assureIdConsistent;
import static com.restaurant_elections.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(AdminDishController.REST_URL)
@Slf4j
@AllArgsConstructor
public class AdminDishController {
    static final String REST_URL = AdminRestaurantController.REST_URL + "/{restaurantId}/dishes";
    private final DishRepository dishRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Dish> getAllInRestaurant(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId) {
        log.info("All dishes from Restaurant id={} requested by {}", restaurantId, authUser);
        return dishRepository.findAllByRestaurantId(restaurantId);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> getInRestaurant(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId,
                                     @PathVariable int id) {
        log.info("Dish id={} from Restaurant id={} requested by {}", id, restaurantId, authUser);
        return ResponseEntity.of(dishRepository.findByIdAndRestaurantId(id, restaurantId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Dish> create(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId,
                                       @Valid @RequestBody Dish dish) {
        log.info("Dish {} in Restaurant id={} create by {}", dish, restaurantId, authUser);
        checkNew(dish);
        dish.setRestaurantId(restaurantId);
        Dish created = dishRepository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void update(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId,
                       @PathVariable int id, @Valid @RequestBody Dish updated) {
        log.info("Dish {} in Restaurant id={} update by {}", updated, restaurantId, authUser);
        assureIdConsistent(updated, id);
        Dish dish = dishRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new NotFoundException("Dish with id=" + id + " in restaurant with id="
                        + restaurantId + " not found"));
        dish.setName(updated.getName());
        dish.setPrice(updated.getPrice());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId,
                       @PathVariable int id) {
        log.info("Dish {} Restaurant id={} delete by {}", id, restaurantId, authUser);
        if (dishRepository.deleteByIdAndRestaurantId(id, restaurantId) == 0) {
           throw new NotFoundException("Dish with id=" + id + " in restaurant with id=" + restaurantId + " not found");
        }
    }
}
