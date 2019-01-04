package com.sommerengineering.popularmovies;

import android.app.LoaderManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Pair<String, URL>>> {

    // constants
    public static final int VIDEOS_LOADER_ID = 1;
    public static final int REVIEWS_LOADER_ID = 2;

    // member variables
    private Context mContext;
    private RelativeLayout mRelativeLayout;
    private ProgressBar mProgressBar;
    private ImageView mPosterIV;
    private int mId;
    private int mViewPositionId;
    private ImageButton mFavoritesStarIB;
    private FavoritesDatabase mDatabase;
    private MovieObject mMovie;
    private boolean mIsFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // initialize the Stetho debugging tool
        Stetho.initializeWithDefaults(this);

        // get a reference for the top level context
        mContext = getApplicationContext();

        // get movie object packaged with explicit intent
        Intent intent = getIntent();
        mMovie = (MovieObject) intent.getSerializableExtra("selectedMovie");

        // get references
        mRelativeLayout = findViewById(R.id.rl_container);
        TextView titleTV = findViewById(R.id.tv_title);
        TextView mPlotTV = findViewById(R.id.tv_plot);
        TextView dateTV = findViewById(R.id.tv_date);
        mPosterIV = findViewById(R.id.iv_poster);
        RatingBar ratingRB = findViewById(R.id.rb_stars);
        mProgressBar = findViewById(R.id.pb_progress);
        mFavoritesStarIB = findViewById(R.id.iv_star);

        // get the movie id
        mId = mMovie.getId();

        // set an initial position for the movie videos (optional) and movie reviews (optional)
        mViewPositionId = R.id.tv_plot;

        // set text
        titleTV.setText(mMovie.getTitle());
        mPlotTV.setText(mMovie.getPlot());
        dateTV.setText(Utilities.formatDate(mMovie.getDate()));

        // critical piece!
        // placeholder image must be set for the Picasso library to load correctly on activity start
        ColorDrawable simpleColor =
                new ColorDrawable(mContext.getResources().getColor(R.color.color_black));

        // set the image view in the grid item layout
        Picasso.with(mContext).load(mMovie.getPosterPath()).placeholder(simpleColor).into(mPosterIV);

        // set rating stars
        ratingRB.setRating((float) mMovie.getRating());

        // initialize a loader manager to handle a background thread
        LoaderManager loaderManager = getLoaderManager();

        // this initialization causes the OS to call onCreateLoader()
        loaderManager.initLoader(VIDEOS_LOADER_ID, null, this);
        loaderManager.initLoader(REVIEWS_LOADER_ID, null, this);

        // handle database operations in a viewmodel
        DetailViewModel viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);

        // get reference to favorites database
        mDatabase = viewModel.getDatabase();

        // livedata list of integer IDs of all favorite movies
        LiveData<List<Integer>> mFavoritesIds = viewModel.getFavoritesIds();

        // define an anonymous observer for the list of favorites IDs
        Observer<List<Integer>> mObserver = new Observer<List<Integer>>() {

            @Override
            public void onChanged(List<Integer> favoriteIds) {

                // loop through all favorite IDs and compare against this movie ID
                for (int i = 0; i < favoriteIds.size(); i++) {

                    // set the image and flag to true since this movie is in the favorites list
                    if (favoriteIds.get(i).equals(mId)) {
                        mFavoritesStarIB.setImageResource(R.drawable.star_filled);
                        mIsFavorite = true;
                    }

                }

            }
        };

        // set the observer on the livedata
        mFavoritesIds.observe(this, mObserver);

        // clicking on the star either inserts a new favorite or deletes an existing one
        mFavoritesStarIB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // single thread executor runs on a background thread
                Executor executor = Executors.newSingleThreadExecutor();

                // movie is in the favorites list
                if (mIsFavorite) {

                    // delete the movie from the database
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mDatabase.favoritesDao().deleteFavoriteMovie(mMovie);
                        }
                    });

                    // set appropriate image and flag
                    mFavoritesStarIB.setImageResource(R.drawable.star_empty);
                    mIsFavorite = false;

                // movie is not in the favorites list
                } else {

                    // insert the movie into the database
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mDatabase.favoritesDao().insertFavoriteMovie(mMovie);
                        }
                    });

                    // set appropriate image and flag
                    mFavoritesStarIB.setImageResource(R.drawable.star_filled);
                    mIsFavorite = true;
                }
            }
        });

    }

    // automatically called when the loader manager determines this loader ID does not exist
    @Override
    public Loader<ArrayList<Pair<String, URL>>> onCreateLoader(int loaderId, Bundle args) {

        // URL for API endpoint "videos" or "reviews"
        URL url;

        // build the URL based on user preference for endpoint
        switch (loaderId) {

            case VIDEOS_LOADER_ID:
                url = Utilities.createDetailsUrl(mId, Utilities.VIDEOS_ENDPOINT);
                break;

            case REVIEWS_LOADER_ID:
                url = Utilities.createDetailsUrl(mId, Utilities.REVIEWS_ENDPOINT);
                break;

            // only two possible loader IDs
            default: return null;
        }

        return new DetailsLoader(this, loaderId, url);

    }

    // automatically called when loader background thread completes
    @Override
    public void onLoadFinished
            (Loader<ArrayList<Pair<String, URL>>> loader, ArrayList<Pair<String, URL>> details) {

        // get the loader id
        int loaderId = loader.getId();

        // check the input exists and is not empty
        if (details != null && !details.isEmpty()) {

            // initialize a tuple variable and a reference for the poster imageview position
            Pair<String, URL> currentPair;
            RelativeLayout.LayoutParams posterLayoutParams;

            switch (loaderId) {

                case VIDEOS_LOADER_ID:

                    // loop through the array of video tuples
                    for (int i = 0; i < details.size(); i++) {

                        // currentPair is a tuple containing the video {title, URL}
                        currentPair = details.get(i);
                        final String currentTitle = currentPair.first;
                        final URL currentUrl = currentPair.second;

                        // each video has a button with the YouTube logo
                        mViewPositionId = createImageButton(R.drawable.play, mViewPositionId, currentUrl);

                        // each video has a textview with the video title
                        createDescriptionTextView(mViewPositionId, currentTitle);

                    }

                    // move poster image below the trailer video buttons
                    posterLayoutParams = (RelativeLayout.LayoutParams) mPosterIV.getLayoutParams();
                    posterLayoutParams.addRule(RelativeLayout.BELOW, mViewPositionId);

                    break;

                case REVIEWS_LOADER_ID:

                    // loop through the array of video tuples
                    for (int i = 0; i < details.size(); i++) {

                        // currentPair is a tuple containing the reviews {content, URL}
                        currentPair = details.get(i);
                        final String currentContent = currentPair.first;
                        final URL currentUrl = currentPair.second;

                        // each video has a button with the YouTube logo
                        mViewPositionId = createImageButton(R.drawable.review, mViewPositionId, currentUrl);

                        // each video has a textview with the video title
                        createDescriptionTextView(mViewPositionId, currentContent);

                    }

                    // move poster image below the review buttons
                    posterLayoutParams = (RelativeLayout.LayoutParams) mPosterIV.getLayoutParams();
                    posterLayoutParams.addRule(RelativeLayout.BELOW, mViewPositionId);

                    break;

            }

        }

        // prevent an automatic refresh by system during activity onResume()
        getLoaderManager().destroyLoader(loaderId);

        // hide the progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

    }

    private int createImageButton(int imageResourceId, int positioningID, final URL currentUrl) {

        ImageButton imageButton = new ImageButton(this);

        // size
        int buttonDimension =
                Utilities.dpToPixels(mContext,
                        getResources().getDimension(R.dimen.button));
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(buttonDimension, buttonDimension);

        // position
        layoutParams.addRule(RelativeLayout.BELOW, positioningID);
        int marginStart =
                Utilities.dpToPixels(mContext, getResources().getDimension(R.dimen.detail_spacing));
        layoutParams.setMarginStart(marginStart);

        // associate the defined size and position parameters with the button
        imageButton.setLayoutParams(layoutParams);

        // let the system create an ID, then get it
        imageButton.setId(View.generateViewId());

        // update ID reference for positioning of (potential) subsequent buttons
        positioningID = imageButton.getId();

        // set image and scale it
        imageButton.setImageResource(imageResourceId);
        imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // click opens YouTube (or other media player) via implicit intent
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl.toString()));
                startActivity(intent);
            }
        });

        // background with ripple effect
        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute
                (android.R.attr.selectableItemBackground, outValue, true);
        imageButton.setBackgroundResource(outValue.resourceId);

        // add button to the layout
        mRelativeLayout.addView(imageButton);

        return positioningID;

    }

    private void createDescriptionTextView(int positioningID, final String currentTitle) {

        TextView descriptionTV = new TextView(this);

        // size and position
        int buttonDimension =
                Utilities.dpToPixels(mContext, getResources().getDimension(R.dimen.button));
        int marginStart =
                Utilities.dpToPixels(mContext, getResources().getDimension(R.dimen.detail_spacing));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, buttonDimension);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, positioningID);
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, positioningID);
        layoutParams.setMarginStart(marginStart);
        layoutParams.setMarginEnd(marginStart);

        // associate the defined size and position parameters with the textview
        descriptionTV.setLayoutParams(layoutParams);

        // let the system create an ID
        descriptionTV.setId(View.generateViewId());

        // basic attributes
        descriptionTV.setText(currentTitle);
        descriptionTV.setGravity(Gravity.CENTER_VERTICAL);
        descriptionTV.setEllipsize(TextUtils.TruncateAt.END);
        descriptionTV.setMaxLines(3);
        descriptionTV.setTextColor(getResources().getColor(R.color.color_black));

        // custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "adamina.ttf");
        descriptionTV.setTypeface(font);

        // add the textview to the layout
        mRelativeLayout.addView(descriptionTV);

    }

    // previously created loader is no longer needed and existing data should be discarded
    @Override
    public void onLoaderReset(Loader<ArrayList<Pair<String, URL>>> loader) {
        // do nothing for now ...
    }

}
