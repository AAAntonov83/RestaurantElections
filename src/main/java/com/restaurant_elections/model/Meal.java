package com.restaurant_elections.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant_elections.HasId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Entity
@Table(name = "meal")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = "menu")
public class Meal extends NamedEntity implements HasId {

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "menu_meals",
            joinColumns = { @JoinColumn(name = "meal_id") },
            inverseJoinColumns = { @JoinColumn(name = "menu_id") }
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Menu> menu;

    @Column
    @Range(max = 10000)
    private int price;

    public Meal(Integer id,  String name, int price) {
        super(id, name);
        this.price = price;
    }
}