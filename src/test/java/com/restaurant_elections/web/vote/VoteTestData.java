package com.restaurant_elections.web.vote;

import com.restaurant_elections.model.Vote;
import com.restaurant_elections.to.VoteTo;
import com.restaurant_elections.web.MatcherFactory;

import java.time.LocalDateTime;

import static com.restaurant_elections.util.DateTimeUtil.atStartOfDay;
import static com.restaurant_elections.web.restaurant.RestaurantTestData.RESTAURANT_2_ID;
import static com.restaurant_elections.web.user.UserTestData.admin;
import static com.restaurant_elections.web.user.UserTestData.user;

public class VoteTestData {
    public static final MatcherFactory.Matcher<Vote> VOTE_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Vote.class, "restaurant", "user");
    public static final MatcherFactory.Matcher<VoteTo> VOTE_TO_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(VoteTo.class);
    public static final int VOTE_1_ID = 1;
    public static final int VOTE_3_ID = 3;
    public static final int VOTE_4_ID = 4;
    public static final int VOTE_NOT_FOUND_ID = 100;
    public static final Vote VOTE_1 = new Vote(VOTE_1_ID, atStartOfDay().plusDays(-1).plusHours(9), user);
    public static final Vote VOTE_3 = new Vote(VOTE_3_ID, atStartOfDay().plusHours(11), admin);
    public static final Vote VOTE_4 = new Vote(VOTE_4_ID, atStartOfDay().plusHours(10), user);
    public static final VoteTo VOTE_TO_4 = new VoteTo(RESTAURANT_2_ID);

    public static Vote getNew() {
        return new Vote(null, LocalDateTime.now());
    }
}
