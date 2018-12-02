package com.sommerengineering.popularmovies;

// custom movie object holds metadata for a specific movie
public class MovieObject {

    // attributes
    private String mTitle; // title
    private String mPosterPath; // URL to poster image
    private String mPlot; // short synopsis of plot
    private double mRating; // aggregated rating between 1-10
    private String mDate; // release date

    // constructor
    public MovieObject(String title, String posterPath, String plot, double rating, String date) {

        mTitle = title;
        mPosterPath = posterPath;
        mPlot = plot;
        mRating = rating;
        mDate = date;

    }

    // getters
    public String getTitle() {
        return mTitle;
    }
    public String getPosterPath() {
        return mPosterPath;
    }
    public String getPlot() {
        return mPlot;
    }
    public double getRating() {
        return mRating;
    }
    public String getDate() {
        return mDate;
    }

}
