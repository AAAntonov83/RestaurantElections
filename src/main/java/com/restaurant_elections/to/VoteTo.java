package com.restaurant_elections.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class VoteTo {
    @NotNull
    int restaurantId;

    public VoteTo(@JsonProperty("restaurantId") int restaurantId) {
        this.restaurantId = restaurantId;
    }
}
