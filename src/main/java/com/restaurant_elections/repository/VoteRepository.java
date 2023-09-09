package com.restaurant_elections.repository;

import com.restaurant_elections.model.Vote;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote> {
}
