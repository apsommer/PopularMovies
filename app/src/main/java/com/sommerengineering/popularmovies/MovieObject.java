package com.sommerengineering.popularmovies;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

// custom movie object holds metadata for a specific movie
@Entity(tableName = "favorites")
class MovieObject implements Serializable {

    // attributes
    @PrimaryKey
    private final int mId; // id

    private final String mTitle; // title
    private final String mThumbnailPath; // URL to poster image
    private final String mPosterPath; // URL to poster image
    private final String mPlot; // short synopsis of plot
    private final double mRating; // aggregated rating between 1-10
    private final String mDate; // release date

    // constructor
    MovieObject(int id, String title, String thumbnailPath, String posterPath, String plot, double rating, String date) {

        mId = id;
        mTitle = title;
        mThumbnailPath = thumbnailPath;
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
    String getThumbnailPath() {
        return mThumbnailPath;
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
