package com.restaurant_elections.web.menu;

import com.restaurant_elections.model.Menu;
import com.restaurant_elections.repository.MenuRepository;
import com.restaurant_elections.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.restaurant_elections.web.dish.DishTestData.*;
import static com.restaurant_elections.web.menu.AdminMenuController.REST_URL;
import static com.restaurant_elections.web.menu.MenuTestData.*;
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
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + MENU_1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_MATCHER.contentJson(MENU_1));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + MENU_NOT_FOUND_ID))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + MENU_1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(repository.findById(MENU_1_ID).isPresent());
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
    void includeDish() throws Exception {
        Menu menu = new Menu(MENU_1_ID, MENU_1.getDate(), DISH_1, DISH_2, DISH_3);
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + MENU_1_ID + "/include-dish/" + DISH_3_ID))
                .andExpect(status().isNoContent());
        assertEquals(menu, repository.getByIdWithDishes(MENU_1_ID).orElse(null));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    void excludeDish() throws Exception {
        Menu menu = new Menu(MENU_1_ID, MENU_1.getDate(), DISH_2);
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + MENU_1_ID + "/exclude-dish/" + DISH_1_ID))
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
