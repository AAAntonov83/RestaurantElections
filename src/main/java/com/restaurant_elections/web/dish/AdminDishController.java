package com.restaurant_elections.web.dish;

import com.restaurant_elections.model.Dish;
import com.restaurant_elections.repository.DishRepository;
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
import java.util.List;

import static com.restaurant_elections.util.validation.ValidationUtil.assureIdConsistent;
import static com.restaurant_elections.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(AdminDishController.REST_URL)
@Slf4j
@AllArgsConstructor
public class AdminDishController {
    static final String REST_URL = "/api/admin/dishes";
    private final DishRepository dishRepository;

    @GetMapping
    public List<Dish> get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("All dishes requested by {}", authUser);
        return dishRepository.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> get(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Dish id={} requested by {}", id, authUser);
        return ResponseEntity.of(dishRepository.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Dish> create(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody Dish dish) {
        log.info("Dish {} create by {}", dish, authUser);
        checkNew(dish);
        Dish created = dishRepository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(AdminDishController.REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id, @Valid @RequestBody Dish updated) {
        log.info("Dish {} update by {}", updated, authUser);
        assureIdConsistent(updated, id);
        dishRepository.save(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Dish {} delete by {}", id, authUser);
        dishRepository.deleteExisted(id);
    }
}
