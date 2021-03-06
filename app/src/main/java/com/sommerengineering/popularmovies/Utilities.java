package com.sommerengineering.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

// final access modifier because no objects of this class will ever be created
// holder for methods related to the network connection
final class Utilities {

    // simple tag for log messages
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    // URL constants
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String TMDB_SCI_FI_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String TMDB_BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    // API constants
    static final String VIDEOS_ENDPOINT = "/videos";
    static final String REVIEWS_ENDPOINT = "/reviews";
    private static final String SCI_FI_ENDPOINT = "&sort_by=vote_count.desc&with_genres=878";
    private static final String API_KEY = "api_key";
    private static final String api_key_personal = BuildConfig.api_key; // TODO add API key here

    // image modifier constants
    private static final String THUMBNAIL_IMAGE_SIZE = "w342/"; // 92, 154, 185, 342, 500, 780, original
    private static final String POSTER_IMAGE_SIZE = "original";

    // create a URL for a set of movies with the given user preference for sort order
    static URL createMoviesUrl(String orderBy) {

        // assemble the full query by compiling constituent parts
        Uri baseUri = Uri.parse(TMDB_BASE_URL + orderBy);

        // prepare URI for appending the query parameters
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // append query parameters, for example "api_key_personal=#"
        uriBuilder.appendQueryParameter(API_KEY, api_key_personal);

        // convert URI to string
        String uriString = uriBuilder.toString();

        // catch a malformed URL
        URL url = null;
        try {
            url = new URL(uriString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    // create a URL for a set of movies with the given user preference for sort order
    static URL createSciFiUrl() {

        // assemble the full query by concatenating constituent parts
        String uriString = TMDB_SCI_FI_BASE_URL + "?" + API_KEY + "=" + api_key_personal + SCI_FI_ENDPOINT;

        // catch a malformed URL
        URL url = null;
        try {
            url = new URL(uriString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    // create a URL for a specific movie to return trailer videos associated with it
    static URL createDetailsUrl(int movieId, String endpoint) {

        // convert int ID to String
        String id = String.valueOf(movieId);

        // assemble the full query by compiling constituent parts
        Uri baseUri = Uri.parse(TMDB_BASE_URL + id + endpoint);

        // prepare URI for appending the query parameters
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // append query parameters, for example "api_key_personal=#"
        uriBuilder.appendQueryParameter(API_KEY, api_key_personal);

        // convert URI to string
        String uriString = uriBuilder.toString();

        // catch a malformed URL
        URL url = null;
        try {
            url = new URL(uriString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    // obtain JSON response string from API endpoint
    public static String getResponseFromHttp(URL url){

        String responseJson = null;

        // HTTP connections potential throws IOException
        try {

            // open the network connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // create an input stream from the opened connection
            InputStream inputStream = urlConnection.getInputStream();

            // "\\A" delimiter denotes the end of server response
            // therefore the entire response is read in one shot
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            // check if there is anything in the stream
            if (scanner.hasNext()) {
                responseJson = scanner.next();
            }

            // close the connection
            urlConnection.disconnect();

        } catch (IOException e) {
            Log.e("~~~~~~~~~~~~~~~~~~~", e.toString());
        }

        return responseJson;

    }

    // convert JSON payload to a list of custom movie objects
    public static ArrayList<MovieObject> extractMoviesFromJson(String responseJson) {

        // initialize an empty ArrayList
        ArrayList<MovieObject> movies = new ArrayList<>();

        // parse raw JSON response string
        try {

            // go down one level of JSON payload
            JSONObject root = new JSONObject(responseJson);
            JSONArray results = root.getJSONArray("results");

            // loop through results (results = movies)
            for (int i = 0; i < results.length(); i++) {

                // current movie
                JSONObject currentResult = results.getJSONObject(i);

                // extract desired metadata from JSON key : value pairs
                int id = currentResult.getInt("id");
                String title = currentResult.getString("title");
                String relativePosterPath = currentResult.getString("poster_path");
                String plot = currentResult.getString("overview");
                double rating = currentResult.getDouble("vote_average");
                String date = currentResult.getString("release_date");

                // the provided image path is relative
                // the base URL and size precede the relative path
                String thumbnailPath = TMDB_BASE_IMAGE_URL + THUMBNAIL_IMAGE_SIZE + relativePosterPath;
                String posterPath = TMDB_BASE_IMAGE_URL + POSTER_IMAGE_SIZE + relativePosterPath;

                // add data to new movie object and store in ArrayList
                movies.add(new MovieObject(id, title, thumbnailPath, posterPath, plot, rating, date));

            }

        // log exception stack trace
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movies JSON results: ", e);
        }

        return movies;

    }

    // convert JSON payload to a list of tuples as {video title, video URL}
    public static ArrayList<Pair<String, URL>> extractVideosFromJson(String responseJson) {

        // initialize an empty tuple and arraylist of tuples
        Pair<String, URL> pair;
        ArrayList<Pair<String, URL>> videos = new ArrayList<>();

        // parse raw JSON response string
        try {

            // go down one level of JSON payload
            JSONObject root = new JSONObject(responseJson);
            JSONArray results = root.getJSONArray("results");

            // loop through results (results = movies)
            for (int i = 0; i < results.length(); i++) {

                // current movie
                JSONObject currentResult = results.getJSONObject(i);

                // extract desired metadata from JSON key : value pairs
                String pathKey = currentResult.getString("key");
                String videoName = currentResult.getString("name");
                String siteName = currentResult.getString("site");

                // only display YouTube trailers for now ...
                if (siteName.equals("YouTube")) {

                    // assemble the full query by compiling constituent parts
                    Uri baseUri = Uri.parse(YOUTUBE_BASE_URL + pathKey);

                    // convert URI to URL and return
                    String uriString = baseUri.toString();

                    // catch a malformed URL
                    URL youTubeUrl = null;
                    try {
                        youTubeUrl = new URL(uriString);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    // each video has a name and a URL, simple tuples
                    pair = new Pair<>(videoName, youTubeUrl);

                    // add data to ArrayList
                    videos.add(pair);

                }

            }

        // log exception stack trace
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the videos JSON results: ", e);
        }

        return videos;

    }

    // convert JSON payload to a list of tuples as {review title, review URL}
    public static ArrayList<Pair<String, URL>> extractReviewsFromJson(String responseJson) {

        // initialize an empty tuple and arraylist of tuples
        Pair<String, URL> pair;
        ArrayList<Pair<String, URL>> reviews = new ArrayList<>();

        // parse raw JSON response string
        try {

            // go down one level of JSON payload
            JSONObject root = new JSONObject(responseJson);
            JSONArray results = root.getJSONArray("results");

            // loop through results (results = movies)
            for (int i = 0; i < results.length(); i++) {

                // current movie
                JSONObject currentResult = results.getJSONObject(i);

                // extract desired metadata from JSON key : value pairs
                String content = currentResult.getString("content");
                String urlString = currentResult.getString("url");

                // assemble the full query by compiling constituent parts
                Uri baseUri = Uri.parse(urlString);

                // convert URI to URL and return
                String uriString = baseUri.toString();

                // catch a malformed URL
                URL reviewUrl = null;
                try {
                    reviewUrl = new URL(uriString);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                // each video has a name and a URL, simple tuples
                pair = new Pair<>(content, reviewUrl);

                // add data to ArrayList
                reviews.add(pair);

            }

        // log exception stack trace
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the reviews JSON results: ", e);
        }

        return reviews;
    }

    // passed to the layout manager of the recycler grid
    // thumbnails will always be approximately the same physical size regardless of device or orientation
    public static int calculateNumberOfColumns(Context context) {

        // get the current device screen dimensions
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        // get current device width in dp
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        // conversion is 160 dp = 1 inch
        float itemWidth = context.getResources().getInteger(R.integer.grid_item);

        // number of columns is how many R.dimen.button can fit in this device width
        return (int) (dpWidth / itemWidth);

    }

    // coverts datetime timestamp to simple date only format
    public static String formatDate(String timestamp) {

        // expected datetime format from JSON
        String expectedPattern = "yyyy-MM-dd";
        SimpleDateFormat expectedFormatter =
                new SimpleDateFormat(expectedPattern, Locale.getDefault());

        // change to a nicer format
        String targetPattern = "MMMM d, yyyy";
        SimpleDateFormat targetFormatter =
                new SimpleDateFormat(targetPattern, Locale.getDefault());

        // unexpected timestamp format will cause parsing error
        try {
            Date date = expectedFormatter.parse(timestamp);
            return targetFormatter.format(date);
        }
        catch (ParseException e) {
            Log.e("~~~~~~~~~~~~~~~~~~~", "Error formatting timestamp.", e);
            return timestamp;
        }

    }

    public static int dpToPixels(Context context, float dp) {
        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

}
