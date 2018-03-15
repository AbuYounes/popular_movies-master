package com.example.android.popularmovies.response;

import com.example.android.popularmovies.model.Trailer;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailersResponse {

    @SerializedName("results")
    private List<Trailer> results;

    public List<Trailer> getResultsTrailer() {
        return results;
    }
}
