package com.sommerengineering.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    // total number of movie items in grid
    private int mNumberOfItems;
    private ArrayList<MovieObject> mMovies;
    private Context mContext;

    public MovieAdapter(Context context, int numberOfItems, ArrayList<MovieObject> movies) {

        // initialize member variables
        mContext = context;
        mNumberOfItems = numberOfItems;
        mMovies = movies;

    }

    // critical component of recycler view that allows view caching (memory optimization)
    class MovieViewHolder extends RecyclerView.ViewHolder {

        // image view for movie poster
        private ImageView mPoster;
        private TextView mTitle;

        // constructor
        public MovieViewHolder(View itemView) {

            // super class initializes the holder
            super(itemView);

            // set member variables
            mPoster = itemView.findViewById(R.id.iv_poster);
            mTitle = itemView.findViewById(R.id.tv_title);

        }

        // called by the adapter
        public void bind(String title) {

            // set the image view in grid_item
            // TODO set ImageView content to URL image for this movie based on item position
            mPoster.setImageResource(R.mipmap.ic_launcher);
            mTitle.setText(title);

        }

    }

    @Override
    public int getItemCount() {
        return mNumberOfItems;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // inflate the grid_item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.grid_item, viewGroup, false);

        // pass the inflated layout to the holder constructor
        MovieViewHolder movieViewHolder = new MovieViewHolder(itemView);

        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        // call into holder class to update the poster image for this item
        if (mMovies != null && mMovies.size() > 0) {
            MovieObject currentMovie = mMovies.get(position);
            String title = currentMovie.getTitle();
            holder.bind(title);
        }

    }

    public void clear() {

        mMovies = null;
    }

    public void addAll(ArrayList<MovieObject> movies) {

        mMovies = movies;

    }

}
