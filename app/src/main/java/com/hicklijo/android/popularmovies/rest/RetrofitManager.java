package com.hicklijo.android.popularmovies.rest;

import com.hicklijo.android.popularmovies.data.Constants;
import com.hicklijo.android.popularmovies.rest.Service.IMovieService;
import com.hicklijo.android.popularmovies.rest.model.MoviesInfo;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Singleton Movie Manager.
public class RetrofitManager {
    private static Retrofit retrofit = null;
    private static IMovieService iMovieService = null;
    private static RetrofitManager retrofitManager = null;

    private RetrofitManager(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        iMovieService = retrofit.create(IMovieService.class);
    }

    public static RetrofitManager getInstance(){
        if(retrofit == null)
        {
            retrofitManager = new RetrofitManager();
        }
        return retrofitManager;
    }


    /*
        Returns information about movies
        @param categories categories (popular, top_rated, etc.
        @param apiKey the api key for themoviedb.com
        @param callback callback for getting response from api
     */
    public void getMoviesInfo(String categories, int page, String apiKey, Callback<MoviesInfo> callback){
        Call<MoviesInfo> moviesInfoCall = iMovieService.getMoviesInfo(categories, page, apiKey);
        moviesInfoCall.enqueue(callback);
    }
}
