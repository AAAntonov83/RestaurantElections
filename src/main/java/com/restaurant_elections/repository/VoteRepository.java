package com.restaurant_elections.repository;

import com.restaurant_elections.model.Restaurant;
import com.restaurant_elections.model.User;
import com.restaurant_elections.model.Vote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote> {

    @Query("SELECT v FROM Vote v WHERE v.id = :id AND v.user = :user")
    Optional<Vote> getByIdAndUser(int id, User user);

    @Query(value = "SELECT v FROM Vote v WHERE v.user = :user AND DAY(v.datetime) = DAY(NOW())")
    Optional<Vote> getByUserToday(User user);

    @Query("SELECT v FROM Vote v LEFT JOIN FETCH v.user WHERE v.restaurant = :restaurant ORDER BY v.datetime DESC")
    List<Vote> findAllByRestaurantWithUser(Restaurant restaurant);
}
