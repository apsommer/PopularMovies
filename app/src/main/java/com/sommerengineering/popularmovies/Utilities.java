package com.sommerengineering.popularmovies;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

// final access modifier because no objects of this class will ever be created
// holder for methods related to the network connection
public final class Utilities {

    // simple tag for log messages
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    // constants
    private static final String THE_MOVIE_DATABASE_BASE_URL = "https://api.themoviedb.org/3/movie/550";


    // returns URL object from a given string URL
    public static URL createUrl(String stringUrl) {

        // initialize returned object to null
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL: ", e);
        }
        return url;
    }


}
