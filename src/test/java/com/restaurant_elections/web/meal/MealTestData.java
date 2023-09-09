package com.restaurant_elections.web.meal;

import com.restaurant_elections.model.Meal;
import com.restaurant_elections.web.MatcherFactory;

public class MealTestData {
    public static final MatcherFactory.Matcher<Meal> MEAL_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Meal.class, "menu");
    public static final int MEAL_1_ID = 1;
    public static final int MEAL_2_ID = 2;
    public static final int MEAL_3_ID = 3;
    public static final int MEAL_NOT_FOUND_ID = 100;
    public static final Meal MEAL_1 = new Meal(MEAL_1_ID, "MEAL_1", 10);
    public static final Meal MEAL_2 = new Meal(MEAL_2_ID, "MEAL_2", 15);
    public static final Meal MEAL_3 = new Meal(MEAL_3_ID, "MEAL_3", 20);

    public static Meal getNew() {
        return new Meal(null, "NewMeal", 0);
    }

    public static Meal getUpdated() {
        return new Meal(MEAL_1_ID, "UpdatedName", MEAL_1.getPrice());
    }
}
