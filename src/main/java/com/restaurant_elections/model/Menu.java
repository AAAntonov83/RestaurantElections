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
import java.util.List;

@Entity
@Table(name = "menu",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"restaurant_id", "date"}, name = "restaurant_date_unique")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

    @JsonIgnore
    @Column(name = "restaurant_id")
    private int restaurantId;

    @Column(name = "date", nullable = false, columnDefinition = "timestamp default now()")
    private LocalDate date;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "menu_meals",
            joinColumns = { @JoinColumn(name = "menu_id")},
            inverseJoinColumns = { @JoinColumn(name = "meal_id")}
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Meal> meals;

    public void addMeal(Meal meal) {
        meals.add(meal);
    }

    public Menu(Integer id, LocalDate date) {
        super(id);
        this.date = date;
    }

    public Menu(Integer id, LocalDate date, Meal... meals) {
        super(id);
        this.date = date;
        this.meals = List.of(meals);
    }

    @Override
    public String toString() {
        return "Menu: " + id + ", {date=" + date + ", meals=" + meals + "}";
    }
}