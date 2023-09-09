package com.restaurant_elections.web.restaurant;

import com.restaurant_elections.error.IllegalRequestDataException;
import com.restaurant_elections.model.Menu;
import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.model.User;
import com.restaurant_elections.model.Vote;
import com.restaurant_elections.repository.MenuRepository;
import com.restaurant_elections.repository.RestaurantRepository;
import com.restaurant_elections.repository.VoteRepository;
import com.restaurant_elections.web.AuthUser;
import com.restaurant_elections.web.vote.VoteController;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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
        return processVote(authUser.getUser(), id, LocalDateTime.now());
    }

    @Hidden
    @Profile("!test")
    @PostMapping(value = "/{id}/test-votes", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Vote> vote(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id, @RequestBody Vote vote) {
        return processVote(authUser.getUser(), id, vote.getDatetime());
    }

    private ResponseEntity<Vote> processVote(User user, int restaurantId, LocalDateTime voteDateTime) {
        log.info("restaurant {} {} vote", restaurantId, user);
        Restaurant restaurant = restaurantRepository.getByIdWithCurrentMenuOnly(restaurantId)
                .orElseThrow(() -> new IllegalRequestDataException("This restaurant not exist or havent menu today"));
        Vote vote = voteRepository.getByUserToday(user).orElse(new Vote(null, null, user));
        if (vote.isNew() || voteDateTime.isBefore(atStartOfDay().plusHours(11))) {
            vote.setDatetime(voteDateTime);
            vote.setRestaurant(restaurant);
            vote = voteRepository.save(vote);
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(VoteController.REST_URL + "/{voteId}")
                    .buildAndExpand(vote.getId()).toUri();
            return ResponseEntity.created(uriOfNewResource).body(vote);
        }
        return ResponseEntity.ok(vote);
    }
}
