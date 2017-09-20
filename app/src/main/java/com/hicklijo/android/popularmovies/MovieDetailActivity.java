package com.hicklijo.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.hicklijo.android.popularmovies.rest.RetrofitManager;
import com.hicklijo.android.popularmovies.rest.model.MovieDetail;
import com.hicklijo.android.popularmovies.rest.model.MoviesInfo;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private MovieDetail mSelectedMovie;
    private TextView mMovieYear;
    private TextView mMovieRunTime;
    private TextView mMovieRating;
    private TextView mMovieTitle;
    private ImageView mPosterImageView;
    private RetrofitManager retrofitManager;
    private Boolean mIsLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mMovieTitle = (TextView) findViewById(R.id.movie_title_text);
        mMovieRunTime = (TextView) findViewById(R.id.movie_runtime_text);
        mMovieYear = (TextView) findViewById(R.id.movie_year_text);
        mMovieRating = (TextView) findViewById(R.id.movie_rating_text);
        mPosterImageView = (ImageView) findViewById(R.id.movie_poster_image_view);


        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity != null){
            if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
                getMovies(intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT));
            }
        }
    }

    private void getMovies(String movieId){
        mIsLoading = true;
        final Context context = this;
        retrofitManager = RetrofitManager.getInstance();

        retrofitManager.getMovieDetail(movieId, BuildConfig.MOVIE_API_KEY, new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        mSelectedMovie = response.body();
                        mMovieTitle.setText(mSelectedMovie.getOriginalTitle());
                        mMovieRunTime.setText(mSelectedMovie.getRuntime().toString());
                        mMovieYear.setText(mSelectedMovie.getReleaseDate());
                        mMovieRating.setText(mSelectedMovie.getVoteCount().toString());

                        Picasso.with(context)
                                .load("http://image.tmdb.org/t/p/w500" + mSelectedMovie.getPosterPath())
                                .placeholder(R.color.colorAccent)
                                .into(mPosterImageView);

                        mIsLoading = false;
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Log.d("MovieDetail :: ", "Failure");
                mIsLoading = false;
            }
        });

    }
}
