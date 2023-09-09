package com.restaurant_elections.web.menu;

import com.restaurant_elections.model.Menu;
import com.restaurant_elections.web.MatcherFactory;

import java.time.LocalDate;

import static com.restaurant_elections.web.meal.MealTestData.MEAL_1;
import static com.restaurant_elections.web.meal.MealTestData.MEAL_2;

public class MenuTestData {
    public static final MatcherFactory.Matcher<Menu> MENU_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Menu.class, "restaurantId");
    public static final int MENU_1_ID = 1;
    public static final int MENU_3_ID = 3;
    public static final int MENU_4_ID = 4;
    public static final int MENU_NOT_FOUND_ID = 100;
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = TODAY.plusDays(-1);
    public static final Menu MENU_1 = new Menu(MENU_1_ID, YESTERDAY, MEAL_1, MEAL_2);
    public static final Menu MENU_3 = new Menu(MENU_3_ID, TODAY, MEAL_1, MEAL_2);
    public static final Menu MENU_4 = new Menu(MENU_4_ID, TODAY, MEAL_1);

    public static Menu getNew() {
        return new Menu(null, TODAY.plusDays(1));
    }

    public static Menu getUpdated() {
        return new Menu(MENU_1_ID, YESTERDAY.plusDays(-1));
    }
}
