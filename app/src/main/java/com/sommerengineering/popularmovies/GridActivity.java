package com.sommerengineering.popularmovies;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

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

        // associate the layout manager and adapter to the recycler view
        mMovieGrid.setLayoutManager(mGridLayoutManager);
        mMovieGrid.setAdapter(mAdapter); // TODO this adapter should be here!

        // the images in the grid will all be the same size
        // explicitly identifying this to the OS allows for performance optimizations
        mMovieGrid.hasFixedSize();

        // TODO check internet connectivity first here ...

        // initialize a loader manager to handle a background thread
        LoaderManager loaderManager = getLoaderManager();

        // this initialization causes the OS to call onCreateLoader()
        loaderManager.initLoader(MOVIE_LOADER_ID, null, this);

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

        // associate the adapter with the recycler grid
        //mMovieGrid.setAdapter(mAdapter);

        // TODO progress bar visibility

        // check the input exists and is not empty
        if (movies != null && !movies.isEmpty()) {

            // calling addAll method on the adapter triggers the recycler grid to update
            mAdapter.addAll(movies);
            mAdapter.notifyDataSetChanged();
        }
        else {

//            // this conditional handles the rare edge case of (1) successful network call (2) populate ListView
//            // (3) leave app (4) lose internet connection (5) return to app
//            if (isConnected()) {
//
//                // the articles list is empty because there are no articles matching the search criteria
//                mEmptyTextView.setText(R.string.no_articles_found);
//
//            }
//
//            // internet connection was lost after a loader with ARTICLE_LOADER_ID was successfully completed
//            else {
//
//                // the articles list is empty because there is no internet connection
//                mEmptyTextView.setText(R.string.no_internet_connection);
//            }

        }

    }

    // previously created loader is no longer needed and existing data should be discarded
    @Override
    public void onLoaderReset(Loader<ArrayList<MovieObject>> loader) {

        // removing all data from adapter automatically clears the UI listview
        mMovieGrid.setAdapter(null);
    }
}
