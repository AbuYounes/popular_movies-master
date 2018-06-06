package com.example.android.popularmovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utils.CheckConnection;
import com.example.android.popularmovies.adapter.MoviesAdapter;
import com.example.android.popularmovies.database.MoviesContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.networkUtils.Client;
import com.example.android.popularmovies.networkUtils.Service;
import com.example.android.popularmovies.response.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.database.MoviesContract.MovieEntry.CONTENT_URI;


public class MoviesMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String WEBPAGE_MOVIE_DB = "https://www.themoviedb.org/";
    private static final String LOG_TAG = MoviesMainActivity.class.getName();
    private static final int TASK_LOADER_ID = 101;

    private MoviesAdapter mAdapter;
    private RecyclerView mRecyclerViewMovies;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mSortOrder;

    private String mPrefOrderKey;
    private String mPrefOrderPop;
    private String mPrefOrderTopRated;
    private String mPrefOrderFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_main);
        initViews();
        if (!CheckConnection.checkInternetConnection(this)) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
    }


    private void initViews() {
        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.recyclerview_movies);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 4));
        }

        //source https://www.youtube.com/watch?v=2lcBx4KVUVk
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_content);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.darker_gray);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMovies();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettings();
        requestMovies();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, MoviesSettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.the_movie_db:
                openWebPage(WEBPAGE_MOVIE_DB);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void loadSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefOrderKey = getString(R.string.pref_sort_order_key);
        mPrefOrderPop = getString(R.string.pref_most_popular);
        mPrefOrderTopRated = getString(R.string.pref_top_rated);
        mPrefOrderFavourite = getString(R.string.pref_favourite);

        mSortOrder = preferences.getString(mPrefOrderKey, mPrefOrderPop);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            Intent intent = new Intent(this, MoviesSettingsActivity.class);
            startActivity(intent);
        } else {
            displayMovieList(movieCursorToArray(data));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void requestMovies() {
        if (mSortOrder.equals(mPrefOrderPop)) {
            requestMoviesFromNetwork("popular");
        } else if (mSortOrder.equals(mPrefOrderTopRated)) {
            requestMoviesFromNetwork("top_rated");
        } else if (mSortOrder.equals(mPrefOrderFavourite)) {
            requestMoviesFromDatabase("favourite");
        }
    }

    // making json request source https://www.youtube.com/watch?v=R4XU8yPzSx0
    private void requestMoviesFromNetwork(String order) {
        //todo show message to users with invalid or no token
        Service apiService = Client.getClient().create(Service.class);
        Call<MoviesResponse> call = apiService.getMovies(order, BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                displayMovieList(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                errorWhileRequestingData();
                Log.d("Error", t.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MoviesMainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestMoviesFromDatabase(String order) {
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    private void displayMovieList(List<Movie> movieList) {
        if (mAdapter == null) {
            mAdapter = new MoviesAdapter(this, movieList);
            mRecyclerViewMovies.setAdapter(mAdapter);
        } else {
            mAdapter.setMovieList(movieList);
        }
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void errorWhileRequestingData() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    //method for opening a webpage
    public void openWebPage(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public List<Movie> movieCursorToArray(Cursor cursor) {
        List<Movie> movieList = new ArrayList<>();
        //Log.d(LOG_TAG, "Found" + cursor.getCount() + " bookmarks");
        if (cursor.getCount() == 0) {
            return movieList;
        }
        if (cursor.moveToFirst()) {
            do {
                //int idIndex = cursor.getColumnIndex(MoviesContract.MovieEntry._ID);
                long movieId = cursor.getLong(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIE_ID));
                String titleIndex = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIE_TITLE));
                String overviewIndex = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIE_OVERVIEW));
                String posterPathIndex = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIE_POSTER_PATH));
                double avgIndex = cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIE_AVG));
                String releaseDateIndex = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIE_RELEASE_DATE));
                Movie movie = new Movie(movieId, titleIndex, posterPathIndex, overviewIndex, releaseDateIndex, avgIndex);
                movieList.add(movie);
            } while (cursor.moveToNext());

        }
        return movieList;
    }
}

