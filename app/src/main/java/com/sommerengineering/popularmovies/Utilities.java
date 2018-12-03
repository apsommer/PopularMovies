package com.sommerengineering.popularmovies;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

// final access modifier because no objects of this class will ever be created
// holder for methods related to the network connection
public final class Utilities {

    // simple tag for log messages
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    // constants
    private static final String THE_MOVIE_DATABASE_BASE_URL = "http://api.themoviedb.org/3/movie/";
//    private static final String SORT_ORDER_POPULAR = "popular";
//    private static final String SORT_ORDER_RATING = "top_rated";
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w342/";

    // query parameters
    private static final String API_KEY = "api_key";
    private static final String api_key = "ae7b929b7942ee2ffc3c8c7d1a7af8cf";
    //http://api.themoviedb.org/3/movie/popular?api_key=ae7b929b7942ee2ffc3c8c7d1a7af8cf

    public static ArrayList<MovieObject> getMovieData(String orderBy) {

        // convert url string input to URL
        URL url = createUrl(orderBy);

        // perform HTTP request to the URL and receive a JSON response back
        String responseJSON = null;
        try {
            responseJSON = getResponseFromHttp(url);
        } catch (IOException e) {
            Log.e("~~~~~~~~~~~~~~~~", e.toString());
        }

        ArrayList<MovieObject> movies = extractMoviesFromJSON(responseJSON);
        return movies;

    }

    // create a URL with the given user preference for sort order
    public static URL createUrl(String orderBy) {

        // assemble the full query by compiling constituent parts
        Uri baseUri = Uri.parse(THE_MOVIE_DATABASE_BASE_URL + orderBy);

        // prepare URI for appending the query parameters
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // append query parameters, for example "api_key=#"
        uriBuilder.appendQueryParameter(API_KEY, api_key);

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

    // obtain JSON response string from API endpoint
    private static String getResponseFromHttp(URL url) throws IOException {

        // open the network connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {

            // create an input stream from the opened connection
            InputStream inputStream = urlConnection.getInputStream();

            // "\\A" delimiter denotes the end of server response
            // therefore the entire response is read in one shot
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            // check if there is anything in the stream
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        // close the connection
        } finally {
            urlConnection.disconnect();
        }

    }

    //
    private static ArrayList<MovieObject> extractMoviesFromJSON(String JSONresponse) {

        // initialize an empty ArrayList
        ArrayList<MovieObject> movies = new ArrayList<>();

        // parse raw JSON response string
        try {

            // go down one level of JSON payload
            JSONObject root = new JSONObject(JSONresponse);
            JSONArray results = root.getJSONArray("results");

            // loop through results (results = movies)
            for (int i = 0; i < results.length(); i++) {

                // current movie
                JSONObject currentResult = results.getJSONObject(i);

                // extract desired metadata from JSON key : value pairs
                String title = currentResult.getString("title");
                String relativePosterPath = currentResult.getString("poster_path");
                String plot = currentResult.getString("overview");
                double rating = currentResult.getDouble("vote_average");
                String date = currentResult.getString("release_date");

                // the provided image path is relative
                // the base URL and size precede the relative path
                String posterPath = BASE_IMAGE_URL + IMAGE_SIZE + relativePosterPath;

                // add data to new movie object and store in ArrayList
                movies.add(new MovieObject(title, posterPath, plot, rating, date));

            }

        } catch (JSONException e) {

            // log exception stack trace
            Log.e(LOG_TAG, "Problem parsing the movie JSON results: ", e);

        }

        return movies;

    }

}
