package com.restaurant_elections.to;

import com.restaurant_elections.HasId;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantTo extends NamedTo implements HasId {

    public RestaurantTo(Integer id, String name) {
        super(id, name);
    }
}
