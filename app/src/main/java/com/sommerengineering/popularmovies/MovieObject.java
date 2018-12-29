package com.sommerengineering.popularmovies;

import java.io.Serializable;

// custom movie object holds metadata for a specific movie
class MovieObject implements Serializable {

    // attributes
    private final int mId; // id
    private final String mTitle; // title
    private final String mPosterPath; // URL to poster image
    private final String mPlot; // short synopsis of plot
    private final double mRating; // aggregated rating between 1-10
    private final String mDate; // release date

    // constructor
    MovieObject(int id, String title, String posterPath, String plot, double rating, String date) {

        mId = id;
        mTitle = title;
        mPosterPath = posterPath;
        mPlot = plot;
        mRating = rating;
        mDate = date;

    }

    // getters
    int getId() {
        return mId;
    }
    String getTitle() {
        return mTitle;
    }
    String getPosterPath() {
        return mPosterPath;
    }
    String getPlot() {
        return mPlot;
    }
    double getRating() {
        return mRating;
    }
    String getDate() {
        return mDate;
    }

}
