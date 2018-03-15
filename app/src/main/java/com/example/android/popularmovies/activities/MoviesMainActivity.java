package com.example.android.popularmovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
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
import com.example.android.popularmovies.Utils.ApiError;
import com.example.android.popularmovies.Utils.CheckConnection;
import com.example.android.popularmovies.Utils.ErrorUtils;
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


public class MoviesMainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String WEBPAGE_MOVIE_DB = "https://www.themoviedb.org/";
    private static final String LOG_TAG = MoviesMainActivity.class.getName();
    private static final int TASK_LOADER_ID = 101;
    public Movie mMovie;
    MoviesAdapter adapter;
    private RecyclerView recyclerViewMovies;
    private ArrayList<Movie> movieList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String sortOrder;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_main);
        initViews();
    }


    private void initViews() {

        recyclerViewMovies = (RecyclerView) findViewById(R.id.recyclerview_movies);


        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerViewMovies.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //source https://www.youtube.com/watch?v=2lcBx4KVUVk
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.darker_gray);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
            }
        });
        CheckConnection.checkInternetConnection(getApplicationContext());
        checkedSortOrder();
    }

    // making json request source https://www.youtube.com/watch?v=R4XU8yPzSx0
    private void jsonRequestPopular() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }

            Service apiService =
                    Client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    if (response.isSuccessful()) {
                        List<Movie> movies = response.body().getResults();
                        recyclerViewMovies.setAdapter(new MoviesAdapter(MoviesMainActivity.this, movies));
                        recyclerViewMovies.smoothScrollToPosition(0);
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } else {
                        ApiError apiError = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), apiError.getMessage(),
                                Toast.LENGTH_SHORT).show();

                    }
                }


                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    //CheckConnection.checkInternetConnection(getApplicationContext());
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /* making json request source https://www.youtube.com/watch?v=R4XU8yPzSx0 && The busy coder's guide to android development
     page 734-737*/
    private void jsonRequestTopRated() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }
            Service apiService =
                    Client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    if (response.isSuccessful()) {
                        List<Movie> movies = response.body().getResults();
                        recyclerViewMovies.setAdapter(new MoviesAdapter(MoviesMainActivity.this, movies));
                        recyclerViewMovies.smoothScrollToPosition(0);
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } else {
                        ApiError apiError = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), apiError.getMessage(),
                                Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    //CheckConnection.checkInternetConnection(getApplicationContext());
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    //method for opening a webpage
    public void openWebPage(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        checkedSortOrder();
    }

    // checks the sortOrder. which gets changed via settings
    private void checkedSortOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            jsonRequestPopular();
        } else if (sortOrder.equals(this.getString(R.string.pref_highest_rated))) {
            jsonRequestTopRated();
        } else if (sortOrder.equals(this.getString(R.string.favorite_movies))) {
            getSupportLoaderManager().initLoader(0, null, this);
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
                if (movieList.size() == 0) {
                    Intent intent = new Intent(this, MoviesSettingsActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (movieList.isEmpty()) {
            checkedSortOrder();
            // re-queries for all tasks
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        }
    }


    private ArrayList<Movie> fetchMoviesFromCursor(Cursor cursor) {
        ArrayList<Movie> result = new ArrayList<>();
        Log.d(LOG_TAG, "Found" + cursor.getCount() + " bookmarks");

        if (cursor.getCount() == 0) {
            return result;
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
                mMovie = new Movie(movieId, titleIndex, posterPathIndex, overviewIndex, releaseDateIndex, avgIndex);

                result.add(mMovie);

            } while (cursor.moveToNext());

        }

        return result;
    }


//    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            // Initialize a Cursor, this will hold all the task data
            ArrayList<Movie> mMovieData;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMovieData);
                } else {
                    // Force a new load
                    mMovieData = new ArrayList<>();
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public ArrayList<Movie> loadInBackground() {
                // Will implement to load data
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    Cursor cursor = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, null);
                    if (cursor != null) {
                        Log.d(LOG_TAG, "Cursor is not null");
                        ArrayList<Movie> res = fetchMoviesFromCursor(cursor);
                        cursor.close();
                        return res;
                    }

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to asynchronously load data. ");
                    e.printStackTrace();
                    return null;
                }
                return null;
            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        adapter = new MoviesAdapter(this, data);
        recyclerViewMovies.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d("test", "cursor count =" + adapter.getItemCount());
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        Log.d(LOG_TAG, "Restarting loader");
    }

}

