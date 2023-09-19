package com.restaurant_elections.util;

import com.restaurant_elections.model.Menu;
import com.restaurant_elections.to.MenuTo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MenusUtil {
    public static List<MenuTo> getTos(Collection<Menu> menus) {
        return menus.stream()
                .map(MenusUtil::createTo)
                .collect(Collectors.toList());
    }

    public static Menu createNewFromTo(MenuTo menuTo) {
        return new Menu(null, menuTo.getDate());
    }

    public static Menu updateFromTo(Menu menu, MenuTo menuTo) {
        menu.setDate(menuTo.getDate());
        return menu;
    }

    public static MenuTo createTo(Menu menu) {
        return new MenuTo(menu.getId(), menu.getDate());
    }
}
