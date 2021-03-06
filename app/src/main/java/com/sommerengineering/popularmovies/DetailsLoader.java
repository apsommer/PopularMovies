package com.sommerengineering.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.util.Pair;

import java.net.URL;
import java.util.ArrayList;

// this loader handles API endpoints for both "videos" and "reviews" associated with a specific movie
class DetailsLoader extends AsyncTaskLoader<ArrayList<Pair<String, URL>>> {

    // initialize member variable for URL address
    private final URL mUrl;
    private final int mLoaderId;

    // constructor
    public DetailsLoader(Context context, int loaderId, URL url) {

        // finish initialization using superclass
        // this loader has only one designated URL address
        super(context);
        mLoaderId = loaderId;
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {

        // force the OS to call loadInBackground()
        forceLoad();
    }

    @Override
    public ArrayList<Pair<String, URL>> loadInBackground() {

        // ensure that the URL exists
        if (mUrl == null) return null;

        // perform the HTTP request for movie data and process the JSON response
        String responseJson = Utilities.getResponseFromHttp(mUrl);

        switch (mLoaderId) {

            // videos
            case DetailActivity.VIDEOS_LOADER_ID:
                return Utilities.extractVideosFromJson(responseJson);

            // reviews
            case DetailActivity.REVIEWS_LOADER_ID:
                return Utilities.extractReviewsFromJson(responseJson);

            // only two possible loader IDs
            default: return null;
        }

    }
}
