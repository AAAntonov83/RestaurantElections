package com.restaurant_elections.to;

import com.restaurant_elections.HasId;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDate;

@Value
@EqualsAndHashCode(callSuper = true)
public class MenuTo extends BaseTo implements HasId {
    LocalDate date;

    public MenuTo(Integer id, LocalDate date) {
        super(id);
        this.date = date;
    }
}
