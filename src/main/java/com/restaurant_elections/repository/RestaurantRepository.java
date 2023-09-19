package com.restaurant_elections.repository;

import com.restaurant_elections.model.Restaurant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Query("SELECT m.restaurant FROM Menu m WHERE m.date = :date")
    List<Restaurant> findAllWithMenuOnDate(LocalDate date);

    @Query("SELECT m.restaurant FROM Menu m WHERE m.date = :date AND m.restaurant.id = :id")
    Optional<Restaurant> getByIdWithMenuOnDateOnly(int id, LocalDate date);
}