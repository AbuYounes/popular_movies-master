package com.example.android.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
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
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.networkUtils.Client;
import com.example.android.popularmovies.networkUtils.Service;
import com.example.android.popularmovies.response.TrailersResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrailerActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterListener {
    private TrailerAdapter mTrailerAdapter;
    private RecyclerView recyclerViewTrailers;
    private int mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        mMovieId = (int) getIntent().getLongExtra("id", -1);

        initViews();

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        recyclerViewTrailers.setLayoutManager(trailerLayoutManager);
    }

    public void initViews() {
        recyclerViewTrailers = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        jsonRequestTrailer();
    }

    // making json request source https://www.youtube.com/watch?v=R4XU8yPzSx0
    private void jsonRequestTrailer() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }

            Service apiService =
                    Client.getClient().create(Service.class);
            Call<TrailersResponse> call = apiService.getTrailer(mMovieId, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailersResponse>() {
                @Override
                public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                    if (response.isSuccessful()) {
                        List<Trailer> trailerList = response.body().getResultsTrailer();
                        mTrailerAdapter = new TrailerAdapter(TrailerActivity.this, trailerList);
                        recyclerViewTrailers.setAdapter(mTrailerAdapter);
                        recyclerViewTrailers.smoothScrollToPosition(0);
                    } else {
                        ApiError apiError = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), apiError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TrailersResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onVideoClick(int position) {
        Trailer trailer = mTrailerAdapter.getTrailerFromPosition(position);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey())));
    }
}
