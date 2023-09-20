package com.restaurant_elections.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vote", indexes = @Index(name = "restaurant_id_index", columnList = "restaurant_id"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseEntity{

    @JsonIgnore
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @JsonIgnore
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date_time", columnDefinition = "timestamp default now()")
    private LocalDateTime datetime;

    public Vote(Integer id, LocalDateTime datetime, Restaurant restaurant, User user) {
        super(id);
        this.datetime = datetime;
        this.restaurant = restaurant;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Vote: " + id + ", datetime=" + datetime;
    }
}