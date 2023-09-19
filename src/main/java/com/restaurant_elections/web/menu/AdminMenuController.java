package com.restaurant_elections.web.menu;

import com.restaurant_elections.error.NotFoundException;
import com.restaurant_elections.model.Dish;
import com.restaurant_elections.model.Menu;
import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.repository.DishRepository;
import com.restaurant_elections.repository.MenuRepository;
import com.restaurant_elections.repository.RestaurantRepository;
import com.restaurant_elections.to.MenuTo;
import com.restaurant_elections.util.MenusUtil;
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
import java.util.Optional;

import static com.restaurant_elections.util.validation.ValidationUtil.assureIdConsistent;
import static com.restaurant_elections.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(AdminMenuController.REST_URL)
@Slf4j
@AllArgsConstructor
public class AdminMenuController {
    public static final String REST_URL = AdminRestaurantController.REST_URL + "/{restaurantId}/menus";
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;

    @GetMapping()
    public List<MenuTo> getAll(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId) {
        log.info("Restaurant id={} all menu requested by {}", restaurantId, authUser);
        Restaurant restaurant = restaurantRepository.getExisted(restaurantId);
        return MenusUtil.getTos(menuRepository.findAllByRestaurant(restaurant.id()));
    }

    @GetMapping(value = "/{menuId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MenuTo> get(@AuthenticationPrincipal AuthUser authUser,
                                    @PathVariable int restaurantId, @PathVariable int menuId) {
        log.info("Menu id={} for restaurant id={} request by {}", menuId, restaurantId, authUser);
        Optional<Menu> menu = menuRepository.getByIdAndRestaurantId(menuId, restaurantId);
        return ResponseEntity.ofNullable(menu.map(MenusUtil::createTo).orElse(null));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MenuTo> create(@AuthenticationPrincipal AuthUser authUser,
                                       @PathVariable int restaurantId, @Valid @RequestBody MenuTo menuTo) {
        log.info("Menu {} for restaurant id={} create by {}", menuTo, restaurantId, authUser);
        checkNew(menuTo);
        Menu newMenu = MenusUtil.createNewFromTo(menuTo);
        newMenu.setRestaurant(restaurantRepository.getExisted(restaurantId));
        Menu created = menuRepository.save(newMenu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(MenusUtil.createTo(created));
    }

    @PutMapping(value = "/{menuId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void update(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId,
                       @PathVariable int menuId, @Valid  @RequestBody MenuTo menuTo) {
        log.info("Menu {} for restaurant {} update by {}", menuTo, restaurantId, authUser);
        assureIdConsistent(menuTo, menuId);
        Menu menu = menuRepository.getByIdAndRestaurantIdWithDishes(restaurantId, menuId)
                .orElseThrow(() -> new NotFoundException("Menu with id=" + menuId + " not found"));
        MenusUtil.updateFromTo(menu, menuTo);
    }

    @DeleteMapping("/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser,
                       @PathVariable int restaurantId,  @PathVariable int menuId) {
        log.info("Menu {} delete by {}", menuId, authUser);
        if (menuRepository.deleteByIdAndRestaurantId(menuId, restaurantId) == 0) {
            throw new NotFoundException("Menu with id=" + menuId + " restaurants id=" + restaurantId + " not found");
        }
    }

    @GetMapping(value = "/{menuId}/dishes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Dish> getAllDishesInMenu(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId,
                                           @PathVariable int menuId) {
        log.info("All dishes from Menu id={} Restaurant id={} requested by {}", menuId, restaurantId, authUser);
        return dishRepository.findAllByRestaurantIdAndMenuId(restaurantId, menuId);
    }

    @GetMapping(value = "/{menuId}/dishes/{dishId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> getDishInMenu(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId,
                                          @PathVariable int menuId, @PathVariable int dishId) {
        log.info("Dish id={} from Menu id={} Restaurant id={} requested by {}", dishId, menuId, restaurantId, authUser);
        return ResponseEntity.of(dishRepository.findByIdAndRestaurantIdAndMenuId(dishId, restaurantId, menuId));
    }

    @PostMapping(value = "/{menuId}/dishes/{dishId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public void includeDish(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId,
                            @PathVariable int menuId, @PathVariable int dishId) {
        log.info("Dish id={} include to Menu id={} by {}", dishId, menuId, authUser);
        Menu menu = menuRepository.getByIdAndRestaurantIdWithDishes(restaurantId, menuId)
                .orElseThrow(() -> new NotFoundException("Menu with id=" + menuId + " not found"));
        menu.addDish(dishRepository.getReferenceById(dishId));
    }

    @DeleteMapping("/{menuId}/dishes/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void excludeDish(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId,
                            @PathVariable int menuId, @PathVariable int dishId) {
        log.info("Dish {} exclude from Menu {} by {}", dishId, menuId, authUser);
        Menu menu = menuRepository.getByIdAndRestaurantIdWithDishes(restaurantId, menuId)
                .orElseThrow(() -> new NotFoundException("Menu with id=" + menuId + " not found"));
        menu.removeDish(dishRepository.getReferenceById(dishId));
    }
}
