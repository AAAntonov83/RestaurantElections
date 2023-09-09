package com.restaurant_elections.web.restaurant;

import com.restaurant_elections.model.Menu;
import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.repository.MenuRepository;
import com.restaurant_elections.repository.RestaurantRepository;
import com.restaurant_elections.util.JsonUtil;
import com.restaurant_elections.web.AbstractControllerTest;
import com.restaurant_elections.web.menu.MenuTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.restaurant_elections.web.menu.MenuTestData.*;
import static com.restaurant_elections.web.restaurant.AdminRestaurantController.REST_URL;
import static com.restaurant_elections.web.restaurant.RestaurantTestData.*;
import static com.restaurant_elections.web.user.UserTestData.ADMIN_MAIL;
import static com.restaurant_elections.web.user.UserTestData.USER_MAIL;
import static com.restaurant_elections.web.vote.VoteTestData.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminRestaurantControllerTest extends AbstractControllerTest {
    static final String REST_URL_SLASH = REST_URL + "/";
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private MenuRepository menuRepository;

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void create() throws Exception {
        Restaurant restaurant = RestaurantTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(restaurant)))
                .andExpect(status().isCreated());

        Restaurant created = RESTAURANT_MATCHER.readFromJson(action);
        int newId = created.id();
        restaurant.setId(newId);
        RESTAURANT_MATCHER.assertMatch(created, restaurant);
        RESTAURANT_MATCHER.assertMatch(restaurantRepository.getExisted(newId), restaurant);
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void createUnsafe() throws Exception {
        Restaurant restaurant = new Restaurant(null, "<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(restaurant)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + RESTAURANT_1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(restaurantRepository.findById(RESTAURANT_1_ID).isPresent());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + RESTAURANT_NOT_FOUND_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        Restaurant updated = RestaurantTestData.getUpdated();
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());

        RESTAURANT_MATCHER.assertMatch(restaurantRepository.getExisted(RESTAURANT_1_ID), RestaurantTestData.getUpdated());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateNotFound() throws Exception {
        Restaurant updated = RestaurantTestData.getUpdated();
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH +  RESTAURANT_NOT_FOUND_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getMenus() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_1_ID + "/menus"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_MATCHER.contentJson(List.of(MENU_3, MENU_1)));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getMenusNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_NOT_FOUND_ID + "/menus"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void createMenu() throws Exception {
        Menu menu = MenuTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_1_ID + "/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isCreated());

        Menu created = MENU_MATCHER.readFromJson(action);
        int newId = created.id();
        menu.setId(newId);
        MENU_MATCHER.assertMatch(created, menu);
        MENU_MATCHER.assertMatch(menuRepository.getExisted(newId), menu);
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void createMenuNotFound() throws Exception {
        Menu menu = MenuTestData.getNew();
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_NOT_FOUND_ID + "/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void updateMenu() throws Exception {
        Menu menu = MenuTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT_1_ID + "/menus/" + MENU_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isNoContent());

        MENU_MATCHER.assertMatch(menu, menuRepository.getExisted(MENU_1_ID));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void updateMenuNotFound() throws Exception {
        Menu menu = MenuTestData.getNew();
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT_NOT_FOUND_ID + "/menus/" + MENU_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getVotes() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_1_ID + "/votes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(new ArrayList<>(List.of(VOTE_3, VOTE_1))));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getVotesNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_NOT_FOUND_ID + "/votes"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }
}
