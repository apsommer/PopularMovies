package com.sommerengineering.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class GridActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<MovieObject>> {

    // constants
    private static final int TOTAL_NUMBER_OF_MOVIES = 20;
    private static final int MOVIE_LOADER_ID = 0;

    // member variables
    private MovieAdapter mAdapter;
    private RecyclerView mMovieGrid;
    private ArrayList<MovieObject> mMovies;
    private GridLayoutManager mGridLayoutManager;
    private ProgressBar mProgressBar;
    private TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // finish initialization with super class and inflate activity_grid layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        // set member variables
        mMovieGrid = findViewById(R.id.rv_recycler);
        mMovies = new ArrayList<>();
        mAdapter = new MovieAdapter(this, TOTAL_NUMBER_OF_MOVIES, mMovies);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mProgressBar = findViewById(R.id.pb_progress);
        mErrorTextView = findViewById(R.id.tv_error);

        // associate the layout manager and adapter to the recycler view
        mMovieGrid.setLayoutManager(mGridLayoutManager);
        mMovieGrid.setAdapter(mAdapter);

        // the images in the grid will all be the same size
        // explicitly identifying this to the OS allows for performance optimizations
        mMovieGrid.hasFixedSize();

        // check for internet connectivity
        if (isConnected()) {

            // initialize a loader manager to handle a background thread
            LoaderManager loaderManager = getLoaderManager();

            // this initialization causes the OS to call onCreateLoader()
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);

        }
        else { // no internet connection

            // hide the progress bar
            mProgressBar.setVisibility(View.GONE);

            // the articles list is empty
            mErrorTextView.setText(R.string.no_internet_connection);

        }

    }

    // initialize overflow menu in top right of Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate menu
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    // called when the settings menu is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // get id of menu item
        int id = item.getItemId();

        // ic_sort icon in top-right of action bar is pressed
        if (id == R.id.action_settings) {

            // explicit Intent to start new SettingsActivity
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        // call through to base class to perform the default menu handling
        return super.onOptionsItemSelected(item);
    }

    // automatically called when the loader manager determines that a loader with an id of
    // MOVIE_LOADER_ID does not exist
    @Override
    public Loader<ArrayList<MovieObject>> onCreateLoader(int id, Bundle args) {

        // TODO load user preferences for sort by order and feed to Utilities.createUrl
        URL url = Utilities.createUrl("arbitrary");
        MovieLoader loader = new MovieLoader(this, url);
        return loader;

    }

    // automatically called when loader background thread completes
    @Override
    public void onLoadFinished(Loader<ArrayList<MovieObject>> loader, ArrayList<MovieObject> movies) {

        // clear the adapter of any previous query results
        mAdapter.clear();

        // hide the progress bar
        mProgressBar.setVisibility(View.GONE);

        // check the input exists and is not empty
        if (movies != null && !movies.isEmpty()) {

            // calling addAll method on the adapter triggers the recycler grid to update
            mAdapter.addAll(movies);
            mAdapter.notifyDataSetChanged();
        }
        else {

            // this conditional handles the rare edge case of the following sequence:
            // (1) successful network call (2) populate recycler grid
            // (3) leave app (4) lose internet connection (5) return to app
            if (isConnected()) {

                // the movie list is empty because no match with search criteria
                mErrorTextView.setText(R.string.no_movies_found);

            }

            // internet connection was lost after a loader successfully completed
            else {

                // the movie list is empty because there is no internet connection
                mErrorTextView.setText(R.string.no_internet_connection);
            }

        }

    }

    // previously created loader is no longer needed and existing data should be discarded
    @Override
    public void onLoaderReset(Loader<ArrayList<MovieObject>> loader) {

        // removing all data from adapter automatically clears the UI listview
        mMovieGrid.setAdapter(null);
    }

    // check status of internet connectivity
    public boolean isConnected() {

        // get internet connectivity status as a boolean
        ConnectivityManager connectivityManager =
            (ConnectivityManager) getApplicationContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE);

        // get network metadata
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        // boolean representing internet is connected, or in progress connecting
        boolean isConnected =
            (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();

        return isConnected;

    }

}
