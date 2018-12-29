package com.sommerengineering.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<MovieObject> {

    // constants
    private static final int DETAIL_MOVIE_LOADER_ID = 1;

    // member variables
    private Context mContext;
    private ProgressBar mProgressBar;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // get a reference for the top level context
        mContext = getApplicationContext();

        // get movie object packaged with explicit intent
        Intent intent = getIntent();
        MovieObject movie = (MovieObject) intent.getSerializableExtra("selectedMovie");

        // get references
        TextView mTitleTV = findViewById(R.id.tv_title);
        TextView mPlotTV = findViewById(R.id.tv_plot);
        TextView mDateTV = findViewById(R.id.tv_date);
        ImageView mPosterIV = findViewById(R.id.iv_poster);
        RatingBar mRatingRB = findViewById(R.id.rb_stars);
        mProgressBar = findViewById(R.id.pb_progress);

        // get the movie id
        mId = movie.getId();

        // set text
        mTitleTV.setText(movie.getTitle());
        mPlotTV.setText(movie.getPlot());
        mDateTV.setText(Utilities.formatDate(movie.getDate()));

        // set poster image and rating stars
        Picasso.with(this).load(movie.getPosterPath()).into(mPosterIV);
        mRatingRB.setRating((float) movie.getRating());

        // initialize a loader manager to handle a background thread
        LoaderManager loaderManager = getLoaderManager();

        // this initialization causes the OS to call onCreateLoader()
        loaderManager.initLoader(DETAIL_MOVIE_LOADER_ID, null, this);

    }

    // automatically called when the loader manager determines that a loader with an id of
    // DETAIL_MOVIE_LOADER_ID does not exist
    @Override
    public Loader<MovieObject> onCreateLoader(int id, Bundle args) {

        // turn on a progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        // TODO check a SQLite database for "favorites"

        // build the URL based on user preference for sort order
        URL url = Utilities.createVideosUrl(mId);

        // pass URL to loader
        // TODO need a new Loader class

        // TODO temporary ...
        return new VideosLoader(this, url);

    }

    // automatically called when loader background thread completes
    @Override
    public void onLoadFinished(Loader<MovieObject> loader, MovieObject movie) {

        // hide the progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

        // check the input exists and is not empty
        if (movie != null) {

            // calling addAll method on the adapter triggers the recycler grid to update
            Log.e("onLoadFinished", "1-2-3");

        }

    }

    // previously created loader is no longer needed and existing data should be discarded
    @Override
    public void onLoaderReset(Loader<MovieObject> loader) {

        // TODO do nothing for now
    }



}
