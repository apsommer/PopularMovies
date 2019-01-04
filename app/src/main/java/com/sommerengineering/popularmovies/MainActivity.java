package com.sommerengineering.popularmovies;

import android.app.LoaderManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<MovieObject>>,
        MoviesAdapter.MovieAdapterOnClickHandler {

    // constants
    private static final int TOTAL_NUMBER_OF_MOVIES = 20;
    private static final int MOVIES_LOADER_ID = 0;

    // member variables
    private MoviesAdapter mAdapter;
    private RecyclerView mRecyclerGrid;
    private ProgressBar mProgressBar;
    private TextView mErrorTextView;
    private Context mContext;
    private GridLayoutManager mGridLayoutManager;
    private LoaderManager mLoaderManager;
    private LiveData<List<MovieObject>> mFavorites;
    private Observer<List<MovieObject>> mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // finish initialization with super class and inflate activity_grid layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        // get a reference for the top level context
        mContext = getApplicationContext();

        // setup the recycler view and adapter
        mRecyclerGrid = findViewById(R.id.rv_recycler);

        // get references to the indicator widgets
        mProgressBar = findViewById(R.id.pb_progress);
        mErrorTextView = findViewById(R.id.tv_error);

        // associate the layout manager and adapter to the recycler view
        mGridLayoutManager = new GridLayoutManager
                (this, Utilities.calculateNumberOfColumns(mContext));
        mRecyclerGrid.setLayoutManager(mGridLayoutManager);

        ArrayList<MovieObject> movies = new ArrayList<>();
        mAdapter = new MoviesAdapter(this, movies, this);
        mRecyclerGrid.setAdapter(mAdapter);

        // the images in the grid will all be the same size
        // explicitly identifying this to the OS allows for performance optimizations
        mRecyclerGrid.hasFixedSize();

        // handle database operations in a viewmodel
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // get the favorites as movie objects
        mFavorites = viewModel.getFavorites();

        // set an observer on the favorites
        mObserver = new Observer<List<MovieObject>>() {
            @Override
            public void onChanged(List<MovieObject> favoriteMovies) {
                mAdapter.addAll((ArrayList<MovieObject>) favoriteMovies);
                mAdapter.notifyDataSetChanged();
            }
        };

        // if connected to the internet initialize a background loader
        if (isConnected()) {
            mLoaderManager = getLoaderManager();
            mLoaderManager.initLoader(MOVIES_LOADER_ID, null, this);
        }

        // no internet connection
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mErrorTextView.setVisibility(View.VISIBLE);
        }

    }

    private void setupViewModel() {

    }

    @Override
    public void onRecyclerItemClick(MovieObject movie) {

        // explicit intent for detail activity
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);

        // add the selected movie to the intent
        intentToStartDetailActivity.putExtra("selectedMovie", movie);

        // start new detail activity
        startActivity(intentToStartDetailActivity);

    }

    // overflow menu top right of Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate menu
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    // items within the overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // get id of selected menu item
        int itemId = item.getItemId();

        // get reference to device persistent shared preferences
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        // get "order-by" preference key string
        String orderByKey = getString(R.string.endpoint_key);

        // create a preference editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // remove the observer set in the view favorites option
        mFavorites.removeObserver(mObserver);

        // switch case for each option
        switch (itemId) {

            case R.id.action_popular:

                // set the "order-by" preference
                editor.putString(orderByKey,
                        getResources().getString(R.string.endpoint_popular));

                // write the new preference to the device
                editor.apply();

                // restart the loader
                mLoaderManager.restartLoader(MOVIES_LOADER_ID, null, this);

                break;


            case R.id.action_top_rated:

                // set the "order-by" preference
                editor.putString(orderByKey,
                        getResources().getString(R.string.endpoint_rating));

                // write the new preference to the device
                editor.apply();

                // restart the loader
                mLoaderManager.restartLoader(MOVIES_LOADER_ID, null, this);

                break;


            case R.id.action_favorites:

                // this method triggers the observer onChange() defined in onCreate()
                // this properly updating the UI while viewing the favorites list
                mFavorites.observe(this, mObserver);

                break;

        }

        // call through to base class to perform the default menu handling
        return super.onOptionsItemSelected(item);
    }

    // automatically called when the loader manager determines this loader ID does not exist
    @Override
    public Loader<ArrayList<MovieObject>> onCreateLoader(int id, Bundle args) {

        // show the progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        // get the user defined persistent preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // retrieve user preference for order-by
        // a reference to the default preference is required by getString
        String orderByKey = getString(R.string.endpoint_key);
        String orderByDefaultValue = getString(R.string.endpoint_default);
        String orderBy = sharedPrefs.getString(orderByKey, orderByDefaultValue);

        // build the URL based on user preference for sort order
        URL url = Utilities.createMoviesUrl(orderBy);

        // pass URL to loader
        return new MoviesLoader(this, url);

    }

    // automatically called when loader background thread completes
    @Override
    public void onLoadFinished(Loader<ArrayList<MovieObject>> loader, ArrayList<MovieObject> movies) {

        // clear the adapter of any previous query results
        mAdapter.clear();

        // check the input exists and is not empty
        if (movies != null && !movies.isEmpty()) {

            // calling addAll method on the adapter triggers the recycler grid to update
            mAdapter.addAll(movies);
            mAdapter.notifyDataSetChanged();

        }

        // prevent an automatic refresh by system during activity onResume()
        getLoaderManager().destroyLoader(MOVIES_LOADER_ID);

        // hide the progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

    }

    // previously created loader is no longer needed and existing data should be discarded
    @Override
    public void onLoaderReset(Loader<ArrayList<MovieObject>> loader) {

        // removing all data from adapter automatically clears the UI listview
        mRecyclerGrid.setAdapter(null);
    }

    // check status of internet connectivity
    private boolean isConnected() {

        // get internet connectivity status as a boolean
        ConnectivityManager connectivityManager =
            (ConnectivityManager) getApplicationContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE);

        // get network metadata
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        // boolean representing internet is connected, or in progress connecting
        return (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();

    }

}
