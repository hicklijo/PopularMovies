package com.hicklijo.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hicklijo.android.popularmovies.rest.RetrofitManager;
import com.hicklijo.android.popularmovies.rest.model.MovieDetail;
import com.hicklijo.android.popularmovies.rest.model.MoviesInfo;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private ImageView mMovieDetailBackdropImageView;
    private TextView mMovieDescription;
    private TextView mMovieTagline;
    private RetrofitManager retrofitManager;
    private Boolean mIsLoading;
    private CollapsingToolbarLayout mToolBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mMovieTitle = findViewById(R.id.movie_title_text);
        mMovieRunTime = findViewById(R.id.movie_runtime_text);
        mMovieYear = findViewById(R.id.movie_year_text);
        mMovieRating = findViewById(R.id.movie_rating_text);
        mPosterImageView = findViewById(R.id.movie_poster_image_view);
        mMovieDetailBackdropImageView = findViewById(R.id.movie_detail_backdrop);
        mMovieDescription = findViewById(R.id.movie_description);
        mMovieTagline = findViewById(R.id.movie_tagline);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolBarLayout =
                findViewById(R.id.toolbar_layout);


        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity != null){
            if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
                getMovies(intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT));
            }else {throw new IllegalArgumentException("Detail activity must contain EXTRA_TEXT");}
        }
    }

    private void getMovies(String movieId){
        mIsLoading = true;

        retrofitManager = RetrofitManager.getInstance();

        try{
            retrofitManager.getMovieDetail(movieId, BuildConfig.MOVIE_API_KEY, new Callback<MovieDetail>() {
                @Override
                public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            mSelectedMovie = response.body();
                            updateDataFields();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MovieDetail> call, Throwable t) {
                    String errorMessage = "Failed to retrieve data from API.  Please check your internet connection";
                    showToast(errorMessage);
                    Log.d("MovieDetail :: ", "Failure");
                    mIsLoading = false;
                }
            });
        } catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    private void updateDataFields(){
        final Context context = this;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date releaseDate = null;
        try {
            releaseDate = formatter.parse(mSelectedMovie.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(releaseDate);

        mMovieTitle.setText(mSelectedMovie.getOriginalTitle());

        mMovieRunTime.setText(String.format(getString(R.string.movie_detail_run_time), mSelectedMovie.getRuntime().toString()));

        mMovieRunTime.setText(String.format(getString(R.string.movie_detail_release_date),mSelectedMovie.getReleaseDate()));

        mMovieRating.setText(String.format(getString(R.string.movie_detail_vote_average), mSelectedMovie.getVoteAverage().toString()));
        mMovieTagline.setText(mSelectedMovie.getTagline());
        mMovieDescription.setText(mSelectedMovie.getOverview());
        mToolBarLayout.setTitle(mSelectedMovie.getTitle() + " - (" + calendar.get(Calendar.YEAR)+")");

        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w500" + mSelectedMovie.getPosterPath())
                .placeholder(R.color.colorAccent)
                .into(mPosterImageView);

        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w500" + mSelectedMovie.getBackdropPath())
                .into(mMovieDetailBackdropImageView);

        mIsLoading = false;
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }
}
