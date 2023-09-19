package com.restaurant_elections.web.restaurant;

import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.repository.RestaurantRepository;
import com.restaurant_elections.to.RestaurantTo;
import com.restaurant_elections.util.RestaurantsUtil;
import com.restaurant_elections.web.AuthUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.restaurant_elections.util.validation.ValidationUtil.assureIdConsistent;
import static com.restaurant_elections.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(AdminRestaurantController.REST_URL)
@Slf4j
@AllArgsConstructor
@Tag(name = "admin restaurant controller")
public class AdminRestaurantController {
    public static final String REST_URL = "/api/admin/restaurants";
    private final RestaurantRepository restaurantRepository;

    @GetMapping()
    public List<RestaurantTo> getAll(@AuthenticationPrincipal AuthUser authUser) {
        log.info("find all restaurants for {}", authUser);
        return RestaurantsUtil.getTos(restaurantRepository.findAll());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Restaurant> create(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody RestaurantTo restaurantTo) {
        log.info("Restaurant {} created by user {}", restaurantTo, authUser);
        checkNew(restaurantTo);
        Restaurant created = restaurantRepository.save(RestaurantsUtil.createNewFromTo(restaurantTo));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(RestaurantController.REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void update(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id, @Valid @RequestBody RestaurantTo updated) {
        log.info("Restaurant {} update by user {}", updated, authUser.id());
        assureIdConsistent(updated, id);
        RestaurantsUtil.updateFromTo(restaurantRepository.getExisted(id), updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "restaurants", allEntries = true)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Restaurant id={} delete by {}", id, authUser);
        restaurantRepository.deleteExisted(id);
    }
}
