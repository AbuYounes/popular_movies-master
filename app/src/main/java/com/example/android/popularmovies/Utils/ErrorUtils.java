package com.example.android.popularmovies.Utils;

import com.example.android.popularmovies.networkUtils.Client;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;


public class ErrorUtils {
    //Error Handling
    public static ApiError parseError(Response<?> response) {
        Converter<ResponseBody, ApiError> converter = Client.
                retrofit.responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiError();
        }
        return error;
    }
}
