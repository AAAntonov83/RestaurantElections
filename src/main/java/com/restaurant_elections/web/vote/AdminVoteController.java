package com.restaurant_elections.web.vote;

import com.restaurant_elections.repository.VoteRepository;
import com.restaurant_elections.web.AuthUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AdminVoteController.REST_URL)
@Slf4j
@AllArgsConstructor
public class AdminVoteController {
    public static final String REST_URL = "/api/votes";
    private final VoteRepository voteRepository;

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("Vote {} delete by User {}", id, authUser.id());
        voteRepository.deleteExisted(id);
    }
}
