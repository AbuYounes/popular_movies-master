package com.example.android.popularmovies.response;


import com.example.android.popularmovies.model.Review;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsResponse {

    @SerializedName("results")
    private List<Review> results;

    public List<Review> getResultsReview() {
        return results;
    }
}
