package com.restaurant_elections.repository;

import com.restaurant_elections.model.Menu;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface MenuRepository extends BaseRepository<Menu> {
}