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

    // constants
    private static final String THE_MOVIE_DATABASE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String VIDEOS_ENDPOINT = "/videos";
    private static final String REVIEWS_ENDPOINT = "/reviews";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String THUMBNAIL_IMAGE_SIZE = "w342/"; // 92, 154, 185, 342, 500, 780, original
    private static final String POSTER_IMAGE_SIZE = "original";

    // query parameters
    private static final String API_KEY = "api_key";
    private static final String api_key = "ae7b929b7942ee2ffc3c8c7d1a7af8cf"; // TODO add API key here

    // create a URL for a set of movies with the given user preference for sort order
    static URL createMoviesUrl(String orderBy) {

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

        return url;

    }

    // create a URL for a specific movie to return trailer videos associated with it
    static URL createVideosUrl(int movieId) {

        // convert int ID to String
        String id = String.valueOf(movieId);

        // assemble the full query by compiling constituent parts
        Uri baseUri = Uri.parse(THE_MOVIE_DATABASE_BASE_URL + id + VIDEOS_ENDPOINT);

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

        return url;

    }

    // create a URL for a specific movie to return reviews associated with it
    static URL createReviewsUrl(int movieId) {

        // convert int ID to String
        String id = String.valueOf(movieId);

        // assemble the full query by compiling constituent parts
        Uri baseUri = Uri.parse(THE_MOVIE_DATABASE_BASE_URL + id + REVIEWS_ENDPOINT);

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
    public static ArrayList<Movie> extractMoviesFromJson(String responseJson) {

        // initialize an empty ArrayList
        ArrayList<Movie> movies = new ArrayList<>();

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
                String thumbnailPath = BASE_IMAGE_URL + THUMBNAIL_IMAGE_SIZE + relativePosterPath;
                String posterPath = BASE_IMAGE_URL + POSTER_IMAGE_SIZE + relativePosterPath;

                // add data to new movie object and store in ArrayList
                movies.add(new Movie(id, title, thumbnailPath, posterPath, plot, rating, date));

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
    // movie thumbnails will always be (roughly) the same size regardless of device or orientation
    public static int calculateNumberOfColumns(Context context) {

        // get the device screen dimensions
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        // device width in dp
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        // number of columns
        // 180 is the magic number ... recall that 160 dp = 1 inch
        int numberOfColumns = (int) (dpWidth / 180);
        return numberOfColumns;
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
