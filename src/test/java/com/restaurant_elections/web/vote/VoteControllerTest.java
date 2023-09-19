package com.restaurant_elections.web.vote;

import com.restaurant_elections.model.Vote;
import com.restaurant_elections.repository.VoteRepository;
import com.restaurant_elections.util.DateTimeUtil;
import com.restaurant_elections.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.restaurant_elections.web.restaurant.RestaurantController.REST_URL;
import static com.restaurant_elections.web.restaurant.RestaurantTestData.*;
import static com.restaurant_elections.web.user.UserTestData.GUEST_MAIL;
import static com.restaurant_elections.web.user.UserTestData.USER_MAIL;
import static com.restaurant_elections.web.vote.VoteTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VoteControllerTest extends AbstractControllerTest {
    private static final String REST_URL_SLASH = REST_URL + '/';
    @Autowired
    private VoteRepository voteRepository;

    @Test
    @WithUserDetails(USER_MAIL)
    void getOnToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "vote"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(VOTE_TO_4));
    }

    @Test
    @WithUserDetails(GUEST_MAIL)
    void create() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_1_ID + "/votes"))
                .andExpect(status().isCreated());

        Vote created = VOTE_MATCHER.readFromJson(action);
        VOTE_MATCHER.assertMatch(created, voteRepository.getExisted(created.id()));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void createAgain() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_1_ID + "/votes"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void update() throws Exception {
        DateTimeUtil.useMockTime(LocalDateTime.of(DateTimeUtil.currentDate(), LocalTime.of(10, 1)));
        Vote vote = new Vote(VOTE_4_ID, VOTE_4.getDatetime(), RESTAURANT_2, VOTE_4.getUser());
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT_2_ID + "/votes"))
                .andExpect(status().isNoContent());

        VOTE_MATCHER.assertMatch(vote, voteRepository.getExisted(VOTE_4_ID));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void updateTooLate() throws Exception {
        DateTimeUtil.useMockTime(LocalDateTime.of(DateTimeUtil.currentDate(), LocalTime.of(20, 1)));
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT_2_ID + "/votes"))
                .andExpect(status().isUnprocessableEntity());

        VOTE_MATCHER.assertMatch(VOTE_4, voteRepository.getExisted(VOTE_4_ID));
    }

    @Test
    @WithUserDetails(GUEST_MAIL)
    void voteWithoutMenu() throws Exception {
        DateTimeUtil.useMockTime(LocalDateTime.of(DateTimeUtil.currentDate(), LocalTime.of(10, 0)));
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_WITHOUT_MENU_AND_VOTE_ID + "/votes"))
                .andExpect(status().isNotFound());
    }
}
