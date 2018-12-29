package com.sommerengineering.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.net.URL;
import java.util.ArrayList;

// loads a list of movies and related metadata using an HTTP request on a background thread
class VideosLoader extends AsyncTaskLoader<MovieObject> {

    // initialize member variable for URL address
    private final URL mUrl;

    // constructor
    public VideosLoader(Context context, URL url) {

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
    public MovieObject loadInBackground() {

        // ensure that the URL exists
        if (mUrl == null) {
            return null;
        }

        // perform the HTTP request for movie data and process the JSON response
        // TODO temporary scaffolding
        return new MovieObject(0, "apples", "bananas", "carrots", "horrible", 1.12, "December 29, 2018");

    }
}
