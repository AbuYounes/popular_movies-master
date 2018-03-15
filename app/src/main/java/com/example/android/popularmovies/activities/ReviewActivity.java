package com.example.android.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utils.ApiError;
import com.example.android.popularmovies.Utils.ErrorUtils;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.networkUtils.Client;
import com.example.android.popularmovies.networkUtils.Service;
import com.example.android.popularmovies.response.ReviewsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    private static final String STATE_REVIEWS = "state reviews";
    private static final String LOG_TAG = ReviewAdapter.class.getName();
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter mReviewAdapter;
    private List<Review> reviewList;
    private int mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mMovieId = (int) getIntent().getLongExtra("id", -1);
        initViews();

        if (savedInstanceState != null) {
            reviewList = savedInstanceState.getParcelableArrayList(STATE_REVIEWS);
            mReviewAdapter.setReviewList(reviewList);
        }
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        recyclerViewReviews.setLayoutManager(reviewLayoutManager);

    }

    public void initViews() {
        recyclerViewReviews = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        jsonRequestReview();
    }

    /* making json request source https://www.youtube.com/watch?v=R4XU8yPzSx0 && The busy coder's guide to android development
   page 734-737*/
    // making json request source https://www.youtube.com/watch?v=R4XU8yPzSx0
    private void jsonRequestReview() {
        //mReview = new Review(mReview.getId(), mReview.getAuthor(), mReview.content);
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }

            Service apiService =
                    Client.getClient().create(Service.class);
            Call<ReviewsResponse> call = apiService.getReview(mMovieId, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<ReviewsResponse>() {
                @Override
                public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                    if (response.isSuccessful()) {
                        List<Review> reviewList = response.body().getResultsReview();
                        mReviewAdapter = new ReviewAdapter(getApplicationContext(), reviewList);
                        recyclerViewReviews.setAdapter(mReviewAdapter);
                        recyclerViewReviews.smoothScrollToPosition(0);
                    } else {
                        ApiError apiError = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), apiError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
