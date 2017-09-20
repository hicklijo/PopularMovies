package com.hicklijo.android.popularmovies.rest.Service;
import com.hicklijo.android.popularmovies.rest.model.MovieDetail;
import com.hicklijo.android.popularmovies.rest.model.MoviesInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IMovieService {
    @GET("3/movie/{categories}")
    Call<MoviesInfo> getMoviesInfo(@Path("categories") String categories, @Query("page") int page, @Query("api_key") String apiKey);

    @GET("3/movie/{movieId}")
    Call<MovieDetail> getMovieDetails(@Path("movieId") String movieId, @Query("api_key") String apiKey);
}

