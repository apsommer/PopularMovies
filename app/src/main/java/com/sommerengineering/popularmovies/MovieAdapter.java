package com.sommerengineering.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    // total number of movie items in grid
    private int mNumberOfItems;

    // critical component of recycler view that allows view caching (memory optimization)
    class MovieViewHolder extends RecyclerView.ViewHolder {

        // constructor
        public MovieViewHolder(View itemView) {
            super(itemView);

            // TODO initialize member variables

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

        // TODO set ImageView content to URL image for this movie based on item position

    }
}
