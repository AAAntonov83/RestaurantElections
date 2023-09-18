package com.restaurant_elections.web.restaurant;

import com.restaurant_elections.model.Menu;
import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.model.Vote;
import com.restaurant_elections.repository.MenuRepository;
import com.restaurant_elections.repository.RestaurantRepository;
import com.restaurant_elections.repository.VoteRepository;
import com.restaurant_elections.web.AuthUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AdminRestaurantController {
    static final String REST_URL = "/api/admin/restaurants";
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final VoteRepository voteRepository;

    @GetMapping()
    public List<Restaurant> getAll(@AuthenticationPrincipal AuthUser authUser) {
        log.info("find all restaurants for {}", authUser);
        return restaurantRepository.findAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Restaurant> create(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody Restaurant restaurant) {
        log.info("Restaurant {} created by user {}", restaurant, authUser);
        checkNew(restaurant);
        Restaurant created = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(RestaurantController.REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id, @Valid @RequestBody Restaurant updated) {
        log.info("Restaurant {} update by user {}", updated, authUser.id());
        assureIdConsistent(updated, id);
        Restaurant restaurant = restaurantRepository.getExisted(id);
        restaurant.setName(updated.getName());
        restaurantRepository.save(restaurant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Restaurant id={} delete by {}", id, authUser);
        restaurantRepository.deleteExisted(id);
    }

    @GetMapping("/{id}/menus")
    public List<Menu> getMenus(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Restaurant id={} all menu requested by {}", id, authUser);
        Restaurant restaurant = restaurantRepository.getExisted(id);
        return menuRepository.findAllByRestaurant(restaurant.id());
    }

    @PostMapping(value = "/{id}/menus", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Menu> createMenu(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id, @Valid  @RequestBody Menu menu) {
        log.info("Menu {} for restaurant {} create by {}", menu, id, authUser);
        checkNew(menu);
        menu.setRestaurant(restaurantRepository.getExisted(id));
        Menu created = menuRepository.save(menu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(RestaurantController.REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{restaurantId}/menus/{menuId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMenu(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId, @PathVariable int menuId, @Valid  @RequestBody Menu menu) {
        log.info("Menu {} for restaurant {} update by {}", menu, restaurantId, authUser);
        assureIdConsistent(menu, menuId);
        menu.setRestaurant(restaurantRepository.getExisted(restaurantId));
        menuRepository.save(menu);
    }

    @GetMapping("/{id}/votes")
    public List<Vote> getVotes(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Restaurant id={} all votes request by u{}", id, authUser);
        Restaurant restaurant = restaurantRepository.getExisted(id);
        return voteRepository.findAllByRestaurantWithUser(restaurant);
    }
}
