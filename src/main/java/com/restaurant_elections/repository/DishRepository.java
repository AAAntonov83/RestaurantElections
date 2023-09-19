package com.restaurant_elections.repository;

import com.restaurant_elections.model.Dish;
import jakarta.persistence.OrderBy;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface DishRepository extends BaseRepository<Dish> {

    @OrderBy("name ASC")
    List<Dish> findAllByRestaurantId(int restaurantId);

    @Query("SELECT m.dishes FROM Menu m WHERE m.restaurant.id = :restaurantId AND m.id = :menuId")
    @OrderBy("name ASC")
    List<Dish> findAllByRestaurantIdAndMenuId(int restaurantId, int menuId);

    Optional<Dish> findByIdAndRestaurantId(int dishId, int restaurantId);

    @Query("SELECT d FROM Dish d RIGHT JOIN FETCH d.menus m WHERE d.id = :dishId AND d.restaurantId = :restaurantId AND m.id = :menuId")
    Optional<Dish> findByIdAndRestaurantIdAndMenuId(int dishId, int restaurantId, int menuId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.id = :id AND d.restaurantId = :restaurantId")
    int deleteByIdAndRestaurantId(int id, int restaurantId);
}