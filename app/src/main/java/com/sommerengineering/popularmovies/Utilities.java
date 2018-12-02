package com.sommerengineering.popularmovies;

import android.net.Uri;
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

    // query parameters
    private static final String API_KEY = "api_key";
    private static final String api_key = ""; // TODO put API key here
    

    // returns URL object from a given string URL
    public static URL createUrl(String sortOrder) {

        // assemble the full query by compiling constituent parts
        Uri baseUri = Uri.parse(THE_MOVIE_DATABASE_BASE_URL);

        // prepare URI for appending the query parameters
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // append query parameters, for example "api_key=#"
        uriBuilder.appendQueryParameter(API_KEY, api_key);
        // TODO implement shared preference to retain user sort selection
        //uriBuilder.appendQueryParameter(... sortOrder);

        // convert URI to URL and return
        String uriString = uriBuilder.toString();

        // catch a malformed URL
        URL url = null;
        try {
            url = new URL(uriString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.e("~~~~~~~~~~~~~~~~~~~~ ", url.toString());

        return url;

    }


}
