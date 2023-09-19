package com.restaurant_elections.repository;

import com.restaurant_elections.model.Restaurant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Cacheable(value = "restaurants", key = "#date")
    @Query("SELECT r FROM Restaurant r RIGHT JOIN FETCH r.menus m WHERE m.date = :date")
    List<Restaurant> findAllWithMenuOnDate(LocalDate date);

    @Query("SELECT r FROM Restaurant r RIGHT JOIN FETCH r.menus m WHERE r.id = :id AND m.date = :date")
    Optional<Restaurant> getByIdWithMenuOnDateOnly(int id, LocalDate date);
}