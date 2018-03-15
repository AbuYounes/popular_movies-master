package com.example.android.popularmovies.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.database.MoviesContract;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.OnClick;

public class MoviesDetailActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private TextView nameOfMovie;
    private TextView plotSynopsis;
    private TextView userRating;
    private TextView releaseDate;
    private ImageView imageView;
    private ImageButton mBookmarkButton;
    private Button mReviewButton;
    private Button mTrailerButton;

    private String movieName, thumbnail, synopsis, rating, dateOfRelease;
    private long movieId;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detail);

        movieId = getIntent().getLongExtra("id", -1);
        init();
        getInfo();
        bookmark();
    }


    public void init() {
        imageView = (ImageView) findViewById(R.id.thumbnail_image_header);
        nameOfMovie = (TextView) findViewById(R.id.title);
        plotSynopsis = (TextView) findViewById(R.id.plotsynopsis);
        userRating = (TextView) findViewById(R.id.userrating);
        releaseDate = (TextView) findViewById(R.id.releasdate);
        mReviewButton = (Button) findViewById(R.id.review_button);
        mBookmarkButton = (ImageButton) findViewById(R.id.bookmark_button);
        mTrailerButton = (Button) findViewById(R.id.trailer_button);


        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(getApplicationContext(), ReviewActivity.class);
                reviewIntent.putExtra("id", movieId);
                startActivity(reviewIntent);
            }
        });

        mTrailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(getApplicationContext(), TrailerActivity.class);
                reviewIntent.putExtra("id", movieId);
                startActivity(reviewIntent);
            }
        });
    }


    public void getInfo() {
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("original_title")) {


            //get all needed extras intent
            thumbnail = getIntent().getExtras().getString("poster_path");
            movieName = getIntent().getExtras().getString("original_title");
            synopsis = getIntent().getExtras().getString("overview");
            rating = getIntent().getExtras().getString("vote_average");
            dateOfRelease = getIntent().getExtras().getString("release_date");


            //setting data to appropriate views
            Picasso.with(this)
                    .load(thumbnail)
                    .placeholder(R.drawable.load)
                    .into(imageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);


        } else {

            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();

        }

    }

    @OnClick(R.id.bookmark_button)
    public void toggleBookmark(View v) {
        Context context = getApplicationContext();
        if (!isBookmarked(context)) {
            if (saveToBookmarks(context)) {
                mBookmarkButton.setImageResource(android.R.drawable.btn_star_big_on);
            }
        } else {
            if (removeFromBookmarks(context)) {
                mBookmarkButton.setImageResource(android.R.drawable.btn_star_big_off);
            }

        }

    }

    public void bookmark() {
        if (isBookmarked(this)) {
            mBookmarkButton.setImageResource(android.R.drawable.btn_star_big_on);

        } else {
            mBookmarkButton.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    public boolean saveToBookmarks(Context context) {
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("original_title")) {
            thumbnail = getIntent().getExtras().getString("poster_path");
            movieName = getIntent().getExtras().getString("original_title");
            synopsis = getIntent().getExtras().getString("overview");
            rating = getIntent().getExtras().getString("vote_average");
            dateOfRelease = getIntent().getExtras().getString("release_date");

        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.MOVIE_ID, movieId);
        contentValues.put(MoviesContract.MovieEntry.MOVIE_TITLE, movieName);
        contentValues.put(MoviesContract.MovieEntry.MOVIE_OVERVIEW, synopsis);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = bos.toByteArray();

        contentValues.put(MoviesContract.MovieEntry.MOVIE_POSTER, bytes);

        contentValues.put(MoviesContract.MovieEntry.MOVIE_POSTER_PATH, thumbnail);
        contentValues.put(MoviesContract.MovieEntry.MOVIE_AVG, rating);
        contentValues.put(MoviesContract.MovieEntry.MOVIE_RELEASE_DATE, dateOfRelease);


        if (context.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues) != null) {
            Toast.makeText(context, R.string.bookmark_added, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, R.string.bookmark_insert_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean removeFromBookmarks(Context context) {
        long deletedRows = context.getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,
                MoviesContract.MovieEntry.MOVIE_ID + "=?", new String[]{Long.toString(movieId)});
        if (deletedRows > 0) {
            Toast.makeText(context, R.string.bookmark_deleted, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, R.string.bookmark_delete_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isBookmarked(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(MoviesContract.MovieEntry.CONTENT_URI,
                        new String[]{MoviesContract.MovieEntry.MOVIE_ID},
                        MoviesContract.MovieEntry.MOVIE_ID + "=?",
                        new String[]{Long.toString(movieId)}, null);
        if (cursor != null) {
            boolean bookmarked = cursor.getCount() > 0;
            cursor.close();
            return bookmarked;
        }
        return false;
    }
}

