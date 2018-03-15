package com.example.android.popularmovies.networkUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static Retrofit retrofit = null;

    //builds the url and converts json into java object via gson-converter
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
