package com.hicklijo.android.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hicklijo.android.popularmovies.Interface.iMovieAdapterOnClickHandler;
import com.hicklijo.android.popularmovies.MovieListActivity;
import com.hicklijo.android.popularmovies.R;
import com.hicklijo.android.popularmovies.rest.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{
    private List<Movie> mMovieList;
    private final iMovieAdapterOnClickHandler mClickHandler;
    private LayoutInflater mInflater;
    private Context mContext;

    public MovieAdapter(Context context, iMovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mClickHandler = clickHandler;
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        MovieAdapterViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie selectedMovie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }
    }

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_movie, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder holder, int position) {
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

    public void setMovieList(List<Movie> movieList) {
        if(this.mMovieList != null ){
            this.mMovieList.addAll(movieList);
        }else{
            this.mMovieList = new ArrayList<>();
            this.mMovieList.addAll(movieList);
        }
        notifyDataSetChanged();
    }

    public void resetMovieList(){
        this.mMovieList = null;
        notifyDataSetChanged();
    }
}
