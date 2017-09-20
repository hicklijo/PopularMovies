package com.hicklijo.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hicklijo.android.popularmovies.Interface.iMovieAdapterOnClickHandler;
import com.hicklijo.android.popularmovies.adapter.MovieAdapter;
import com.hicklijo.android.popularmovies.data.Constants;
import com.hicklijo.android.popularmovies.rest.RetrofitManager;
import com.hicklijo.android.popularmovies.rest.model.Movie;
import com.hicklijo.android.popularmovies.rest.model.MoviesInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieListActivity extends AppCompatActivity implements iMovieAdapterOnClickHandler {
    private RecyclerView mRecyleView;
    private MovieAdapter mAdapter;
    private RetrofitManager retrofitManager;
    private MoviesInfo mMoviesInfo;
    private int mCurrentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        mRecyleView = findViewById(R.id.recyclerView);
        mRecyleView.setLayoutManager( new GridLayoutManager(this, 3));
        mAdapter = new MovieAdapter(this, this);
        mRecyleView.setAdapter(mAdapter);

        //Pagination
        mRecyleView.addOnScrollListener(recylerViewOnScrollListener);

        getMovies(mCurrentPage);
    }

    private RecyclerView.OnScrollListener recylerViewOnScrollListener =
            new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    GridLayoutManager layoutManager = ((GridLayoutManager)mRecyleView.getLayoutManager());
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= Constants.PAGE_SIZE) {
                            getMovies(mCurrentPage);
                        }
                    }
                }
            };


    @Override
    public void onClick(Movie selectedMovie){
        Context context = this;
        Class desintationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, desintationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, selectedMovie.getId().toString());
        startActivity(intentToStartDetailActivity);
    }
    private void getMovies(int page){
        isLoading = true;
        retrofitManager = RetrofitManager.getInstance();

        retrofitManager.getMoviesInfo("popular",page, BuildConfig.MOVIE_API_KEY,new Callback<MoviesInfo>(){
            @Override
            public void onResponse(Call<MoviesInfo> call, Response<MoviesInfo> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        mMoviesInfo = response.body();
                        mCurrentPage = response.body().getPage();
                        if(mCurrentPage == response.body().getTotalPages()){
                            isLastPage = true;
                        }

                        mAdapter.setMovieList(response.body().getResults());
                        mCurrentPage++;
                        isLoading = false;
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesInfo> call, Throwable t) {
                Log.d("UserList :: ", "Failure");
                isLoading = false;
            }
        });
    }
}
