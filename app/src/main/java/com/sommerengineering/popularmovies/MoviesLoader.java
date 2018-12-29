package com.sommerengineering.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.net.URL;
import java.util.ArrayList;

// loads a list of movies and related metadata using an HTTP request on a background thread
class MoviesLoader extends AsyncTaskLoader<ArrayList<MovieObject>> {

    // initialize member variable for URL address
    private final URL mUrl;

    // constructor
    public MoviesLoader(Context context, URL url) {

        // finish initialization using superclass
        super(context);

        // this loader has only one designated URL address
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {

        // required to force the OS to call loadInBackground()
        forceLoad();
    }

    @Override
    public ArrayList<MovieObject> loadInBackground() {

        // ensure that the URL exists
        if (mUrl == null) {
            return null;
        }

        // perform the HTTP request for movie data and process the JSON response
        String responseJson = Utilities.getResponseFromHttp(mUrl);
        ArrayList<MovieObject> movies = Utilities.extractMoviesFromJSON(responseJson);
        return movies;

    }
}