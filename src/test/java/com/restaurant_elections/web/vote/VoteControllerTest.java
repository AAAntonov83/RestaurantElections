package com.restaurant_elections.web.vote;

import com.restaurant_elections.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.restaurant_elections.web.user.UserTestData.ADMIN_MAIL;
import static com.restaurant_elections.web.user.UserTestData.USER_MAIL;
import static com.restaurant_elections.web.vote.VoteController.REST_URL;
import static com.restaurant_elections.web.vote.VoteTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VoteControllerTest extends AbstractControllerTest {
    private static final String REST_URL_SLASH = REST_URL + '/';

    @Test
    @WithUserDetails(USER_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + VOTE_4_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(VOTE_TO_4));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getAnotherUserVoice() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + VOTE_4_ID))
                .andExpect(status().isNotFound());
    }
}
