package com.sommerengineering.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

public class GridActivity extends AppCompatActivity {

    // constants
    private static final int TOTAL_NUMBER_OF_MOVIES = 100;

    // member variables
    private MovieAdapter mAdapter;
    private RecyclerView mMovieGrid;
    private LinearLayoutManager mLayoutManager; // TODO find best format for LayoutManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // finish initialization with super class and inflate activity_grid layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        // set member variables
        mMovieGrid = findViewById(R.id.rv_recycler);
        mAdapter = new MovieAdapter(TOTAL_NUMBER_OF_MOVIES);
        mLayoutManager = new LinearLayoutManager(this); // TODO find best format for LayoutManager

        // associate the layout manager and adapter to the recycler view
        mMovieGrid.setLayoutManager(mLayoutManager);
        mMovieGrid.setAdapter(mAdapter);

        // the images in the grid will all be the same size
        // explicitly identifying this to the OS allows for performance optimzations
        mMovieGrid.hasFixedSize();

            // TODO temp checks of utility methods
            URL url = Utilities.createUrl("apples");

            // perform HTTP request to the URL and receive a JSON response back
            String responseJSON = null;
            try {
                responseJSON = Utilities.getResponseFromHttp(url);
            } catch (IOException e) {
                Log.e("~~~~~~~~~~~~~~~~", e.toString());
            }

    }
}
