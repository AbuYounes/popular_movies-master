package com.example.android.popularmovies.networkUtils;


import com.example.android.popularmovies.response.MoviesResponse;
import com.example.android.popularmovies.response.ReviewsResponse;
import com.example.android.popularmovies.response.TrailersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {
    //relative url of the source with query parameter asynchonously via Call

    //
    @GET("movie/{order}")
    Call<MoviesResponse> getMovies(@Path("order") String order, @Query("api_key") String apiKey);



    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailersResponse> getTrailer(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewsResponse> getReview(@Path("id") int id, @Query("api_key") String apiKey);
}
