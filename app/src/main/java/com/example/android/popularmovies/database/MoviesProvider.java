package com.example.android.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class MoviesProvider extends ContentProvider {

    public static final int FAVORITE_MOVIE = 101;
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDBHelper mMoviesDBHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIE, FAVORITE_MOVIE);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMoviesDBHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor favoriteMovieCursor;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE_MOVIE: {
                SQLiteDatabase db = mMoviesDBHelper.getReadableDatabase();
                favoriteMovieCursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        favoriteMovieCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return favoriteMovieCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri returnUri = null;

        switch (sUriMatcher.match(uri)) {
            case FAVORITE_MOVIE:
                SQLiteDatabase db = mMoviesDBHelper.getWritableDatabase();
                long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(id);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case FAVORITE_MOVIE: {
                SQLiteDatabase db = mMoviesDBHelper.getWritableDatabase();
                return db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
        //TODO 1 update this callback
    }
}
