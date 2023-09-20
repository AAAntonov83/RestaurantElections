package com.restaurant_elections.repository;

import com.restaurant_elections.model.User;
import com.restaurant_elections.model.Vote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote> {

    @Query("SELECT v FROM Vote v LEFT JOIN FETCH v.restaurant WHERE v.user = :user AND v.datetime >= :startOfDay AND v.datetime <  :startOfNextDay")
    Optional<Vote> getByUserOnDate(User user, LocalDateTime startOfDay, LocalDateTime startOfNextDay);
}
