package com.restaurant_elections.repository;

import com.restaurant_elections.model.Menu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface MenuRepository extends BaseRepository<Menu> {

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.dishes WHERE m.id = :id")
    Optional<Menu> getByIdWithDishes(int id);

    @Query(value =
            "SELECT * FROM menu m " +
                    "LEFT JOIN menu_dishes dishes ON m.id = dishes.menu_id " +
                    "WHERE m.restaurant_id = :id AND m.date = DATE_TRUNC(DAY, NOW())", nativeQuery = true)
    Optional<Menu> getCurrentForRestaurant(int id);

    @Query("SELECT m FROM Menu m WHERE m.restaurant.id = :id ORDER BY m.date DESC")
    List<Menu> findAllByRestaurant(int id);
}