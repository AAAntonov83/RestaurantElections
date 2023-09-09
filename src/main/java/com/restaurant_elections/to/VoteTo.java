package com.restaurant_elections.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class VoteTo {
    @NotNull
    int restaurant_id;

    public VoteTo(@JsonProperty("restaurant_id") int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }
}
