package com.restaurant_elections.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "menu",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"restaurant_id", "on_date"}, name = "restaurant_date_unique")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "on_date", nullable = false, columnDefinition = "date default now()")
    private LocalDate date;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "menus")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Dish> dishes;

    public void addDish(Dish dish) {
        dishes.add(dish);
    }

    public void removeDish(Dish dish) {
        dishes.remove(dish);
    }

    public Menu(Integer id, LocalDate date) {
        super(id);
        this.date = date;
    }

    public Menu(Integer id, LocalDate date, Dish... dishes) {
        super(id);
        this.date = date;
        this.dishes = Set.of(dishes);
    }

    @Override
    public String toString() {
        return "Menu: " + id + ", date=" + date + "}";
    }
}