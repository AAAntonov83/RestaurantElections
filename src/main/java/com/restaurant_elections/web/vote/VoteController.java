package com.restaurant_elections.web.vote;

import com.restaurant_elections.error.NotFoundException;
import com.restaurant_elections.model.Vote;
import com.restaurant_elections.repository.VoteRepository;
import com.restaurant_elections.to.VoteTo;
import com.restaurant_elections.web.AuthUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(VoteController.REST_URL)
@Slf4j
@AllArgsConstructor
public class VoteController {
    public static final String REST_URL = "/api/votes";
    private final VoteRepository voteRepository;

    @GetMapping("/{id}")
    public VoteTo get(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Vote {} get {}", id, authUser);
        Vote vote = voteRepository.getByIdAndUser(id, authUser.getUser()).orElseThrow(() -> new NotFoundException("Vote not found"));
        return new VoteTo(vote.getRestaurant().id());
    }
}
