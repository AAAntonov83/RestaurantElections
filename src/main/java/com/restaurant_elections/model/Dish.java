package com.restaurant_elections.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "dish", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "dish_name_unique")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Dish extends NamedEntity {

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "menu_dishes",
            joinColumns = { @JoinColumn(name = "dish_id")},
            inverseJoinColumns = { @JoinColumn(name = "menu_id")}
    )
    private List<Menu> menus;

    @Column
    private int price;

    public Dish(Integer id, String name, int price) {
        super(id, name);
        this.price = price;
    }
}