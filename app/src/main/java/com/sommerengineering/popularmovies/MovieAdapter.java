package com.sommerengineering.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    // total number of movie items in grid
    private int mNumberOfItems;

    public MovieAdapter(int numberOfItems) {

        // initialize member variables
        mNumberOfItems = numberOfItems;

    }

    // critical component of recycler view that allows view caching (memory optimization)
    class MovieViewHolder extends RecyclerView.ViewHolder {

        // image view for movie poster
        private ImageView mMoviePoster;

        // constructor
        public MovieViewHolder(View itemView) {

            // super class initializes the holder
            super(itemView);

            // set member variables
            mMoviePoster = itemView.findViewById(R.id.iv_poster);

        }

        // called by the adapter
        public void bind(int position) {

            // set the image view in grid_item
            // TODO set ImageView content to URL image for this movie based on item position
            mMoviePoster.setImageResource(R.mipmap.ic_launcher);

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
        holder.bind(position);

    }
}
