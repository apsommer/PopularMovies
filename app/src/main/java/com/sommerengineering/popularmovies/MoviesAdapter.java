package com.sommerengineering.popularmovies;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    // member variables
    private ArrayList<MovieObject> mMovies;
    private final Context mContext;
    private final MovieAdapterOnClickHandler mClickHandler;

    // constructor
    public MoviesAdapter(Context context,
                         ArrayList<MovieObject> movies, MovieAdapterOnClickHandler clickHandler) {

        // initialize member variables
        mContext = context;
        mMovies = movies;
        mClickHandler = clickHandler;

    }

    // a single handler is called on the click event of a single recycler grid item
    public interface MovieAdapterOnClickHandler {

        // only one method required to override
        void onRecyclerItemClick(MovieObject movie);
    }

    // critical component of recycler view that allows view caching (memory optimization)
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // image view for movie poster
        private final ImageView mThumbnail;

        // constructor
        MovieViewHolder(View itemView) {

            // super class initializes the holder
            super(itemView);

            // set member variables
            mThumbnail = itemView.findViewById(R.id.iv_thumbnail);

            // set click listener on view
            itemView.setOnClickListener(this);

        }

        // click event on this item view
        @Override
        public void onClick(View v) {

            // get the clicked movie and pass to the custom handler
            int position = getAdapterPosition();
            MovieObject movie = mMovies.get(position);
            mClickHandler.onRecyclerItemClick(movie);

        }

        // called by the adapter
        void bind(String thumbnailPath) {

            // critical piece!
            // placeholder image must be set for the Picasso library to load correctly on app start
            ColorDrawable simpleColor =
                    new ColorDrawable(mContext.getResources().getColor(R.color.color_black));

            // set the image view in the grid item layout
            Picasso.with(mContext).load(thumbnailPath).placeholder(simpleColor).into(mThumbnail);

        }

    }

    @Override
    public int getItemCount() {

        if (mMovies == null) return 0;
        return mMovies.size();

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // inflate the grid_item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.grid_item, viewGroup, false);

        // pass the inflated layout to the holder constructor
        return new MovieViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {


        // check that movie objects actually exists
        if (mMovies != null && mMovies.size() > 0) {

            // get current movie
            MovieObject currentMovie = mMovies.get(position);

            // call into holder class to update the poster image for this item
            String thumbnailPath = currentMovie.getThumbnailPath();
            holder.bind(thumbnailPath);

            Log.e("~~", currentMovie.toString());

        }

    }

    // clear adapter
    public void clear() {
        mMovies = null;
    }

    // update adapter
    public void addAll(ArrayList<MovieObject> movies) {
        mMovies = movies;
    }

}
