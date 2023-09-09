package com.restaurant_elections.web.vote;

import com.restaurant_elections.repository.VoteRepository;
import com.restaurant_elections.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.restaurant_elections.web.user.UserTestData.ADMIN_MAIL;
import static com.restaurant_elections.web.vote.AdminVoteController.REST_URL;
import static com.restaurant_elections.web.vote.VoteTestData.VOTE_3_ID;
import static com.restaurant_elections.web.vote.VoteTestData.VOTE_NOT_FOUND_ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminVoteControllerTest extends AbstractControllerTest {
    private static final String REST_URL_SLASH = REST_URL + '/';

    @Autowired
    private VoteRepository repository;

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + VOTE_3_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(repository.findById(VOTE_3_ID).isPresent());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + VOTE_NOT_FOUND_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
