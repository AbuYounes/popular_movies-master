package com.example.android.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MoviesDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PopularMovies.db";
    private static final int DB_VERSION = 6;

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_QUERY =
                "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + "(" +
                        MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.MovieEntry.MOVIE_ID + " INTEGER , " +
                        MoviesContract.MovieEntry.MOVIE_TITLE + " TEXT , " +
                        MoviesContract.MovieEntry.MOVIE_OVERVIEW + " TEXT , " +
                        MoviesContract.MovieEntry.MOVIE_POSTER + " BLOB , " +
                        MoviesContract.MovieEntry.MOVIE_POSTER_PATH + " TEXT , " +
                        MoviesContract.MovieEntry.MOVIE_AVG + " REAL , " +
                        MoviesContract.MovieEntry.MOVIE_RELEASE_DATE + " TEXT " +
                        ")";

        db.execSQL(SQL_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
