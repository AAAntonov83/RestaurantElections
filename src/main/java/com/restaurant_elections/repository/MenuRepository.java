package com.restaurant_elections.repository;

import com.restaurant_elections.model.Menu;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface MenuRepository extends BaseRepository<Menu> {

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.dishes WHERE m.id = :id")
    Optional<Menu> getByIdWithDishes(int id);

    @Query("SELECT m FROM Menu m WHERE m.id = :menuId AND m.restaurant.id = :restaurantId ")
    Optional<Menu> getByIdAndRestaurantId(int restaurantId, int menuId);

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.dishes WHERE m.id = :menuId AND m.restaurant.id = :restaurantId ")
    Optional<Menu> getByIdAndRestaurantIdWithDishes(int restaurantId, int menuId);

    @Query(value =
            "SELECT m FROM Menu m LEFT JOIN FETCH m.dishes dishes WHERE m.restaurant.id = :restaurantId AND m.date = :date")
    Optional<Menu> getByRestaurantIdWithDishesOnDate(int restaurantId, LocalDate date);

    @Query("SELECT m FROM Menu m WHERE m.restaurant.id = :id ORDER BY m.date DESC")
    List<Menu> findAllByRestaurant(int id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Menu m WHERE m.id = :id AND m.restaurant.id = :restaurantId")
    int deleteByIdAndRestaurantId(int id, int restaurantId);
}