package com.restaurant_elections.repository;

import com.restaurant_elections.model.Restaurant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Query(value =
            """
            SELECT r.id, r.name 
            FROM Restaurant r RIGHT JOIN Menu m 
            ON r.id = m.restaurant_id 
            WHERE m.date = DATE_TRUNC(DAY, NOW())""", nativeQuery = true)
    List<Restaurant> findAllWithCurrentMenuOnly();

    @Query(value =
            """
            SELECT r.id, r.name 
            FROM Restaurant r RIGHT JOIN Menu m 
            ON r.id = m.restaurant_id 
            WHERE r.id = :id AND m.date = DATE_TRUNC(DAY, NOW())""", nativeQuery = true)
    Optional<Restaurant> getByIdWithCurrentMenuOnly(int id);
}