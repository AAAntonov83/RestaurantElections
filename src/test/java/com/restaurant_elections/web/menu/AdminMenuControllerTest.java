package com.restaurant_elections.web.menu;

import com.restaurant_elections.model.Menu;
import com.restaurant_elections.repository.MenuRepository;
import com.restaurant_elections.to.MenuTo;
import com.restaurant_elections.util.JsonUtil;
import com.restaurant_elections.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.restaurant_elections.web.dish.DishTestData.*;
import static com.restaurant_elections.web.menu.MenuTestData.*;
import static com.restaurant_elections.web.restaurant.AdminRestaurantController.REST_URL;
import static com.restaurant_elections.web.restaurant.RestaurantTestData.RESTAURANT_1_ID;
import static com.restaurant_elections.web.restaurant.RestaurantTestData.RESTAURANT_NOT_FOUND_ID;
import static com.restaurant_elections.web.user.UserTestData.ADMIN_MAIL;
import static com.restaurant_elections.web.user.UserTestData.USER_MAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminMenuControllerTest extends AbstractControllerTest {
    static final String REST_URL_SLASH = REST_URL + "/";

    @Autowired
    private MenuRepository repository;

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getAllByRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_1_ID + "/menus"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_MATCHER.contentJson(List.of(MENU_3, MENU_1)));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getAllRestaurantNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_NOT_FOUND_ID + "/menus"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void create() throws Exception {
        Menu menu = MenuTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_1_ID + "/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(new MenuTo(menu.getId(), menu.getDate()))))
                .andExpect(status().isCreated());

        Menu created = MENU_MATCHER.readFromJson(action);
        int newId = created.id();
        menu.setId(newId);
        MENU_MATCHER.assertMatch(created, menu);
        MENU_MATCHER.assertMatch(repository.getExisted(newId), menu);
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void createNotFound() throws Exception {
        Menu menu = MenuTestData.getNew();
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_NOT_FOUND_ID + "/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void update() throws Exception {
        Menu menu = MenuTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT_1_ID + "/menus/" + MENU_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isNoContent());

        MENU_MATCHER.assertMatch(menu, repository.getExisted(MENU_1_ID));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void updateNotFound() throws Exception {
        Menu menu = MenuTestData.getNew();
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + RESTAURANT_NOT_FOUND_ID + "/menus/" + MENU_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "/" + RESTAURANT_1_ID + "/menus/" + MENU_1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_MATCHER.contentJson(MENU_1));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "/" + RESTAURANT_1_ID + "/menus/" + MENU_NOT_FOUND_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + "/" + RESTAURANT_1_ID + "/menus/" + MENU_1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(repository.getByIdAndRestaurantId(MENU_1_ID, RESTAURANT_1_ID).isPresent());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + MENU_NOT_FOUND_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getAllInMenu() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_1_ID + "/menus/" + MENU_1_ID + "/dishes"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(List.of(DISH_1, DISH_2)));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getInMenu() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + RESTAURANT_1_ID + "/menus/" + MENU_1_ID + "/dishes/" + DISH_1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(DISH_1));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void includeDish() throws Exception {
        Menu menu = new Menu(MENU_1_ID, MENU_1.getDate(), DISH_1, DISH_2, DISH_5_NOT_IN_MENU);
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + RESTAURANT_1_ID + "/menus/" + MENU_1_ID + "/dishes/" + DISH_5_NOT_IN_MENU_ID))
                .andExpect(status().isCreated());
        assertEquals(menu, repository.getByIdWithDishes(MENU_1_ID).orElse(null));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void excludeDish() throws Exception {
        Menu menu = new Menu(MENU_1_ID, MENU_1.getDate(), DISH_2);
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + RESTAURANT_1_ID + "/menus/" + MENU_1_ID + "/dishes/" + DISH_1_ID))
                .andExpect(status().isNoContent());
        assertEquals(menu, repository.getByIdWithDishes(MENU_1_ID).orElse(null));
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
