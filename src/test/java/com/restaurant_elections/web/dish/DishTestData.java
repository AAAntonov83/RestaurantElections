package com.restaurant_elections.web.dish;

import com.restaurant_elections.model.Dish;
import com.restaurant_elections.web.MatcherFactory;

public class DishTestData {
    public static final MatcherFactory.Matcher<Dish> DISH_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Dish.class, "menu");
    public static final int DISH_1_ID = 1;
    public static final int DISH_2_ID = 2;
    public static final int DISH_3_ID = 3;
    public static final int DISH_NOT_FOUND_ID = 100;
    public static final Dish DISH_1 = new Dish(DISH_1_ID, "DISH_1", 10);
    public static final Dish DISH_2 = new Dish(DISH_2_ID, "DISH_2", 15);
    public static final Dish DISH_3 = new Dish(DISH_3_ID, "DISH_3", 20);

    public static Dish getNew() {
        return new Dish(null, "NewDish", 0);
    }

    public static Dish getUpdated() {
        return new Dish(DISH_1_ID, "UpdatedName", DISH_1.getPrice());
    }
}
