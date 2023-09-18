package com.restaurant_elections.web.restaurant;

import com.restaurant_elections.model.Vote;
import com.restaurant_elections.repository.VoteRepository;
import com.restaurant_elections.util.JsonUtil;
import com.restaurant_elections.web.AbstractControllerTest;
import com.restaurant_elections.web.vote.VoteTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.restaurant_elections.web.menu.MenuTestData.MENU_4;
import static com.restaurant_elections.web.menu.MenuTestData.MENU_MATCHER;
import static com.restaurant_elections.web.restaurant.RestaurantController.REST_URL;
import static com.restaurant_elections.web.restaurant.RestaurantTestData.*;
import static com.restaurant_elections.web.user.UserTestData.GUEST_MAIL;
import static com.restaurant_elections.web.user.UserTestData.USER_MAIL;
import static com.restaurant_elections.web.vote.VoteTestData.VOTE_4;
import static com.restaurant_elections.web.vote.VoteTestData.VOTE_MATCHER;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantControllerTest extends AbstractControllerTest {
    static final String REST_URL_SLASH = REST_URL + "/";
    @Autowired
    private VoteRepository voteRepository;

    @Test
    @WithUserDetails(USER_MAIL)
    void getAllHavingMenuToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/with-menu"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(List.of(RESTAURANT_1, RESTAURANT_2)));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(RESTAURANT_1));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_NOT_FOUND_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void getCurrentMenu() throws Exception {
        perform(MockMvcRequestBuilders.get(String.format("%s%d/%s", REST_URL_SLASH, RESTAURANT_2_ID, "menus")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_MATCHER.contentJson(MENU_4));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void getCurrentMenuForMissingRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(String.format("%s%d/%s", REST_URL_SLASH, RESTAURANT_NOT_FOUND_ID, "menus")))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void getCurrentMenuForRestaurantWithoutMenu() throws Exception {
        perform(MockMvcRequestBuilders.get(String.format("%s%d/%s", REST_URL_SLASH, RESTAURANT_WITHOUT_MENU_AND_VOTE_ID, "menus")))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(GUEST_MAIL)
    void vote() throws Exception {
        Vote vote = VoteTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_1_ID + "/test-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(vote)))
                .andExpect(status().isCreated());

        Vote created = VOTE_MATCHER.readFromJson(action);
        vote.setId(created.getId());
        VOTE_MATCHER.assertMatch(vote, created);
        VOTE_MATCHER.assertMatch(vote, voteRepository.getExisted(vote.id()));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void voteAgainLate() throws Exception {
        Vote vote = VoteTestData.getNew();
        vote.setDatetime(LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0)));
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_2_ID + "/test-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(vote)))
                .andExpect(VOTE_MATCHER.contentJson(VOTE_4))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void voteAgainEarly() throws Exception {
        Vote vote = VoteTestData.getNew();
        vote.setDatetime(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)));
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_1_ID + "/test-votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(vote)))
                .andExpect(status().isCreated());

        Vote created = VOTE_MATCHER.readFromJson(action);
        vote.setId(created.getId());
        VOTE_MATCHER.assertMatch(vote, created);
        VOTE_MATCHER.assertMatch(vote, voteRepository.getExisted(vote.id()));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void voteWithoutMenu() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_WITHOUT_MENU_AND_VOTE_ID + "/votes"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }
}
