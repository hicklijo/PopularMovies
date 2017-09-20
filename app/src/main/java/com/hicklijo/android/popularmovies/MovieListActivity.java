package com.hicklijo.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    private String mMovieSortCategory = BuildConfig.CATEGORY_HIGHEST_RATED;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_by_most_popular) {
            mMovieSortCategory = BuildConfig.CATEGORY_MOST_POPULAR;
            resetView();
            getMovies(mCurrentPage, mMovieSortCategory);
            return true;
        }
        else if(id == R.id.action_sort_by_top_rated){
            mMovieSortCategory = BuildConfig.CATEGORY_HIGHEST_RATED;
            resetView();
            getMovies(mCurrentPage, mMovieSortCategory);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void resetView(){
        mAdapter.resetMovieList();
        mCurrentPage = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyleView = findViewById(R.id.recyclerView);
        mRecyleView.setLayoutManager( new GridLayoutManager(this, 3));
        mAdapter = new MovieAdapter(this, this);
        mRecyleView.setAdapter(mAdapter);

        //Pagination
        mRecyleView.addOnScrollListener(recylerViewOnScrollListener);

        getMovies(mCurrentPage, mMovieSortCategory);
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
                            getMovies(mCurrentPage, mMovieSortCategory);
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
    private void getMovies(int page, String category){
        isLoading = true;
        retrofitManager = RetrofitManager.getInstance();

        retrofitManager.getMoviesInfo(category,page, BuildConfig.MOVIE_API_KEY,new Callback<MoviesInfo>(){
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
                String errorMessage = "Failed to retrieve data from API.  Please check your internet connection";
                showToast(errorMessage);
                Log.d("MoviesInfo :: ", "Failure");
                isLoading = false;
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }
}
