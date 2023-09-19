package com.restaurant_elections.web.vote;

import com.restaurant_elections.error.IllegalRequestDataException;
import com.restaurant_elections.error.NotFoundException;
import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.model.Vote;
import com.restaurant_elections.repository.RestaurantRepository;
import com.restaurant_elections.repository.VoteRepository;
import com.restaurant_elections.to.VoteTo;
import com.restaurant_elections.util.DateTimeUtil;
import com.restaurant_elections.web.AuthUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.restaurant_elections.util.DateTimeUtil.atStartOfDay;
import static com.restaurant_elections.util.DateTimeUtil.atStartOfNextDay;
import static com.restaurant_elections.web.restaurant.RestaurantController.REST_URL;

@RestController
@RequestMapping(REST_URL)
@Slf4j
@AllArgsConstructor
public class VoteController {
    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;

    @GetMapping("vote")
    public ResponseEntity<VoteTo> getOnToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("Getting a vote for today by {} ", authUser);
        LocalDateTime currentDateTime = DateTimeUtil.currentDateTime();
        Optional<Vote> vote = voteRepository.getByUserOnDate(authUser.getUser(),
                atStartOfDay(currentDateTime), atStartOfNextDay(currentDateTime));
        VoteTo voteTo = vote.map(v -> new VoteTo(v.getRestaurant().id())).orElse(null);
        return ResponseEntity.ofNullable(voteTo);
    }

    @PostMapping("/{restaurantId}/votes")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Vote create(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId) {
        log.info("Vote for restaurant id={} by {} ", restaurantId, authUser);
        LocalDateTime currentDateTime = DateTimeUtil.currentDateTime();
        Restaurant restaurant = restaurantRepository.getByIdWithMenuOnDateOnly(restaurantId, DateTimeUtil.currentDate())
                .orElseThrow(() -> new NotFoundException("Not found restaurant id="+ restaurantId + "with menu on today"));
        Optional<Vote> vote = voteRepository.getByUserOnDate(authUser.getUser(),
                atStartOfDay(currentDateTime), atStartOfNextDay(currentDateTime));
        if (vote.isEmpty()) {
            return voteRepository.save(new Vote(null, currentDateTime, restaurant, authUser.getUser()));
        } else {
            throw new IllegalRequestDataException("User voted today already.");
        }
    }

    @PutMapping("/{restaurantId}/votes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void update(@AuthenticationPrincipal AuthUser authUser, @PathVariable int restaurantId) {
        log.info("Updating vote for restaurant id={} by {} ", restaurantId, authUser);
        LocalDateTime currentDateTime = DateTimeUtil.currentDateTime();
        Restaurant restaurant = restaurantRepository.getByIdWithMenuOnDateOnly(restaurantId, DateTimeUtil.currentDate())
                .orElseThrow(() -> new NotFoundException("Not found restaurant id="+ restaurantId + "with menu on today"));
        Vote vote = voteRepository.getByUserOnDate(authUser.getUser(),
                        atStartOfDay(currentDateTime), atStartOfNextDay(currentDateTime))
                .orElseThrow(() -> new IllegalRequestDataException("User has not voted yet today."));

        if (currentDateTime.isAfter(atStartOfDay(currentDateTime).plusHours(11))) {
            throw new IllegalRequestDataException("Voice update is possible until 11 am.");
        }
        vote.setRestaurant(restaurant);
        //voteRepository.save(newVote);
    }
}
