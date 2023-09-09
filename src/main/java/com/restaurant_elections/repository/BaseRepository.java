package com.restaurant_elections.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.id=:id")
    int delete(int id);

    default void deleteExisted(int id) {
        if (delete(id) == 0) {
            throw new NoSuchElementException("Entity with id=" + id + " not found");
        }
    }

    default T getExisted(int id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException("Entity with id=" + id + " not found"));
    }
}