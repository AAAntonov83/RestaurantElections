package com.restaurant_elections.repository;

import com.restaurant_elections.model.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {
}