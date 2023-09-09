package com.restaurant_elections.web.restaurant;

import com.restaurant_elections.error.IllegalRequestDataException;
import com.restaurant_elections.model.Menu;
import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.model.Vote;
import com.restaurant_elections.repository.MenuRepository;
import com.restaurant_elections.repository.RestaurantRepository;
import com.restaurant_elections.repository.VoteRepository;
import com.restaurant_elections.web.AuthUser;
import com.restaurant_elections.web.vote.VoteController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.restaurant_elections.util.DateTimeUtil.atStartOfDay;

@RestController
@RequestMapping(RestaurantController.REST_URL)
@Slf4j
@AllArgsConstructor
public class RestaurantController {
    static final String REST_URL = "/api/restaurants";
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final VoteRepository voteRepository;

    @GetMapping
    public List<Restaurant> getAllWithCurrentMenuOnly(@AuthenticationPrincipal AuthUser authUser) {
        log.info("find all restaurants with current menu for {}", authUser);
        return restaurantRepository.findAllWithCurrentMenuOnly();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> get(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("get restaurant {} for {}", id, authUser);
        Optional<Restaurant> restaurant = Optional.of(restaurantRepository.getExisted(id));
        return ResponseEntity.of(restaurant);
    }

    @GetMapping("/{id}/menus")
    public ResponseEntity<Menu> getCurrentMenu(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("get current restaurant {} menu for {}", id, authUser);
        Optional<Menu> menu = menuRepository.getCurrentForRestaurant(id);
        return ResponseEntity.of(menu);
    }

    @PostMapping("/{id}/votes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Vote> vote(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("restaurant {} {} vote", id, authUser);
        Restaurant restaurant = restaurantRepository.getByIdWithCurrentMenuOnly(id)
                .orElseThrow(() -> new IllegalRequestDataException("This restaurant havent menu today"));
        Vote vote = voteRepository.getByUserToday(authUser.getUser()).orElse(new Vote(null, null, authUser.getUser()));
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (vote.isNew() || currentDateTime.isBefore(atStartOfDay().plusHours(11))) {
            vote.setDatetime(currentDateTime);
            vote.setRestaurant(restaurant);
            Vote created = voteRepository.save(vote);
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(VoteController.REST_URL + "/votes/{voteId}")
                    .buildAndExpand(created.getId()).toUri();
            return ResponseEntity.created(uriOfNewResource).body(created);
        }
        return ResponseEntity.of(Optional.of(vote));
    }

    @PostMapping(value = "/{id}/test-votes", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Vote> vote(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id, @RequestBody Vote vote) {
        log.info("restaurant {} {} vote", id, authUser);
        Restaurant restaurant = restaurantRepository.getByIdWithCurrentMenuOnly(id)
                .orElseThrow(() -> new IllegalRequestDataException("This restaurant havent menu today"));
        Vote oldVote = voteRepository.getByUserToday(authUser.getUser()).orElse(new Vote(null, null, authUser.getUser()));
        if (oldVote.isNew() || vote.getDatetime().isBefore(atStartOfDay().plusHours(11))) {
            oldVote.setDatetime(vote.getDatetime());
            oldVote.setRestaurant(restaurant);
            Vote created = voteRepository.save(oldVote);
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(VoteController.REST_URL + "/{voteId}")
                    .buildAndExpand(created.getId()).toUri();
            return ResponseEntity.created(uriOfNewResource).body(created);
        }
        return ResponseEntity.of(Optional.of(oldVote));
    }
}
