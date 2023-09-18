package com.restaurant_elections.web.restaurant;

import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.web.MatcherFactory;

public class RestaurantTestData {
    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "menus", "votes");
    public static final int RESTAURANT_1_ID = 1;
    public static final int RESTAURANT_2_ID = 2;
    public static final int RESTAURANT_WITHOUT_MENU_AND_VOTE_ID = 3;
    public static final int RESTAURANT_NOT_FOUND_ID = 100;
    public static final Restaurant RESTAURANT_1 = new Restaurant(RESTAURANT_1_ID, "RESTAURANT_1");
    public static final Restaurant RESTAURANT_2 = new Restaurant(RESTAURANT_2_ID, "RESTAURANT_2");
    public static final Restaurant RESTAURANT_WITHOUT_MENU = new Restaurant(RESTAURANT_WITHOUT_MENU_AND_VOTE_ID, "RESTAURANT_WITHOUT_MENU");

    public static Restaurant getNew() {
        return new Restaurant(null, "NewRestaurant");
    }

    public static Restaurant getUpdated() {
        return new Restaurant(RESTAURANT_1_ID, "UpdatedName");
    }
}
