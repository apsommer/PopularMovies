package com.sommerengineering.popularmovies;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.app.UiAutomation;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Pair<String, URL>>> {

    // constants
    private static final int DETAIL_MOVIE_LOADER_ID = 1;

    // member variables
    private Context mContext;
    private RelativeLayout mRelativeLayout;
    private ProgressBar mProgressBar;
    private TextView mPlotTV;
    private ImageView mPosterIV;
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
        mRelativeLayout = findViewById(R.id.rl_container);
        TextView titleTV = findViewById(R.id.tv_title);
        mPlotTV = findViewById(R.id.tv_plot);
        TextView dateTV = findViewById(R.id.tv_date);
        mPosterIV = findViewById(R.id.iv_poster);
        RatingBar ratingRB = findViewById(R.id.rb_stars);
        mProgressBar = findViewById(R.id.pb_progress);

        // get the movie id
        mId = movie.getId();

        // set text
        titleTV.setText(movie.getTitle());
        mPlotTV.setText(movie.getPlot());
        dateTV.setText(Utilities.formatDate(movie.getDate()));

        // set poster image and rating stars
        Picasso.with(this).load(movie.getPosterPath()).into(mPosterIV);
        ratingRB.setRating((float) movie.getRating());

        // initialize a loader manager to handle a background thread
        LoaderManager loaderManager = getLoaderManager();

        // this initialization causes the OS to call onCreateLoader()
        loaderManager.initLoader(DETAIL_MOVIE_LOADER_ID, null, this);

    }

    // automatically called when the loader manager determines that a loader with an id of
    // DETAIL_MOVIE_LOADER_ID does not exist
    @Override
    public Loader<ArrayList<Pair<String, URL>>> onCreateLoader(int id, Bundle args) {

        // turn on a progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        // TODO check a SQLite database for "favorites"

        // build the URL based on user preference for sort order
        URL url = Utilities.createVideosUrl(mId);

        // pass URL to loader
        return new VideosLoader(this, url);

    }

    // automatically called when loader background thread completes
    @Override
    public void onLoadFinished(Loader<ArrayList<Pair<String,
            URL>>> loader, ArrayList<Pair<String, URL>> videos) {

        // hide the progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

        // check the input exists and is not empty
        if (videos != null) {

            Pair<String, URL> currentPair;

            // loop through the array of video tuples
            for (int i = 0; i < videos.size(); i++) {

                // tuple is (video title, video URL)
                currentPair = videos.get(i);
                Log.e("~~~~~~~~~", currentPair.toString());

                // TODO ImageButton
                ImageButton youtubeIB = new ImageButton(this);

                // size
                int buttonDimension =
                        Utilities.dpToPixels(mContext,
                        getResources().getDimension(R.dimen.youtube_button_size));
                RelativeLayout.LayoutParams layoutParams =
                        new RelativeLayout.LayoutParams(buttonDimension, buttonDimension);

                // position
                layoutParams.addRule(RelativeLayout.BELOW, R.id.tv_plot);
                int marginStart =
                        Utilities.dpToPixels(mContext,
                        getResources().getDimension(R.dimen.detail_spacing));
                layoutParams.setMarginStart(marginStart);

                // associate the defined size and position parameters with the button
                youtubeIB.setLayoutParams(layoutParams);

                // basic attributes
                youtubeIB.setId(View.generateViewId());
                youtubeIB.setImageResource(R.drawable.play);
                youtubeIB.setScaleType(ImageView.ScaleType.FIT_CENTER);

                // background with ripple effect
                TypedValue outValue = new TypedValue();
                mContext.getTheme().resolveAttribute
                        (android.R.attr.selectableItemBackground, outValue, true);
                youtubeIB.setBackgroundResource(outValue.resourceId);

                // add the button to the layout
                mRelativeLayout.addView(youtubeIB);

                // TODO TextView
                TextView descriptionTV = new TextView(this);

                // size and position
                layoutParams = new RelativeLayout.LayoutParams
                        (ViewGroup.LayoutParams.WRAP_CONTENT, buttonDimension);
                layoutParams.addRule(RelativeLayout.RIGHT_OF, youtubeIB.getId());
                layoutParams.addRule(RelativeLayout.ALIGN_TOP, youtubeIB.getId());
                layoutParams.setMarginStart(marginStart);

                // associate the defined size and position parameters with the textview
                descriptionTV.setLayoutParams(layoutParams);

                // basic attributes
                descriptionTV.setId(View.generateViewId());
                descriptionTV.setText(currentPair.first);
                descriptionTV.setGravity(Gravity.CENTER_VERTICAL);

                Typeface font = Typeface.createFromAsset(getAssets(), "adamina.ttf");
                descriptionTV.setTypeface(font);

                // add the textview to the layout
                mRelativeLayout.addView(descriptionTV);

                // TODO position the poster below everything
                // move poster image below the trailer video buttons
                RelativeLayout.LayoutParams posterLayoutParams =
                        (RelativeLayout.LayoutParams) mPosterIV.getLayoutParams();

                posterLayoutParams.addRule(RelativeLayout.BELOW, youtubeIB.getId());

            }


        }

    }

    // previously created loader is no longer needed and existing data should be discarded
    @Override
    public void onLoaderReset(Loader<ArrayList<Pair<String, URL>>> loader) {

        // TODO do nothing for now
    }



}
