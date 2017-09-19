package com.hicklijo.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hicklijo.android.popularmovies.data.Constants;
import com.hicklijo.android.popularmovies.rest.RetrofitManager;
import com.hicklijo.android.popularmovies.rest.model.MoviesInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.hicklijo.android.popularmovies.rest.model.Movie;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieListActivity extends AppCompatActivity {
    private RecyclerView mRecyleView;
    private MoviesAdapter mAdapter;
    private RetrofitManager retrofitManager;
    private MoviesInfo mMoviesInfo;
    private int mCurrentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mRecyleView = findViewById(R.id.recyclerView);
        mRecyleView.setLayoutManager( new GridLayoutManager(this, 3));
        mAdapter = new MoviesAdapter(this);
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


    private static class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private MovieViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    private static class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
        private List<Movie> mMovieList;
        private LayoutInflater mInflater;
        private Context mContext;

        private MoviesAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            View view = mInflater.inflate(R.layout.row_movie, parent, false);
            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {
            Movie movie = mMovieList.get(position);
            Picasso.with(mContext)
                    .load("http://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                    .placeholder(R.color.colorAccent)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return (mMovieList == null) ? 0 : mMovieList.size();
        }

        private void setMovieList(List<Movie> movieList) {
            if(this.mMovieList != null ){
                this.mMovieList.addAll(movieList);
            }else{
                this.mMovieList = new ArrayList<>();
                this.mMovieList.addAll(movieList);
            }

            notifyDataSetChanged();
        }
    }
}
