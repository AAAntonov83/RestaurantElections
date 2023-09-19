package com.restaurant_elections.web.restaurant;

import com.restaurant_elections.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.restaurant_elections.web.menu.MenuTestData.MENU_4_WITH_DISHES;
import static com.restaurant_elections.web.menu.MenuTestData.MENU_MATCHER;
import static com.restaurant_elections.web.restaurant.RestaurantController.REST_URL;
import static com.restaurant_elections.web.restaurant.RestaurantTestData.*;
import static com.restaurant_elections.web.user.UserTestData.USER_MAIL;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantControllerTest extends AbstractControllerTest {
    static final String REST_URL_SLASH = REST_URL + "/";

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
        perform(MockMvcRequestBuilders.get(String.format("%s%d/%s", REST_URL_SLASH, RESTAURANT_2_ID, "menu")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_MATCHER.contentJson(MENU_4_WITH_DISHES));
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
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }
}
