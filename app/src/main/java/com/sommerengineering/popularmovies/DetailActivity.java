package com.sommerengineering.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private TextView mTitleTV;
    private TextView mPlotTV;
    private TextView mDateTV;
    private ImageView mThumbnailIV;
    private RatingBar mRatingRB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // get movie object packaged with explicit intent
        Intent intent = getIntent();
        MovieObject movie = (MovieObject) intent.getSerializableExtra("selectedMovie");

        // get references
        mTitleTV = findViewById(R.id.tv_title);
        mPlotTV = findViewById(R.id.tv_plot);
        mDateTV = findViewById(R.id.tv_date);
        mThumbnailIV = findViewById(R.id.iv_thumbnail);
        mRatingRB = findViewById(R.id.rb_stars);

        // set content
        mTitleTV.setText(movie.getTitle());
        mPlotTV.setText(movie.getPlot());
        mDateTV.setText(formatDate(movie.getDate()));
        Picasso.with(this).load(movie.getPosterPath()).into(mThumbnailIV);
        mRatingRB.setRating((float) movie.getRating());

    }

    // coverts datetime timestamp to simple date only format
    private String formatDate(String timestamp) {

        // expected datetime format
        String expectedPattern = "yyyy-MM-dd";
        SimpleDateFormat expectedFormatter = new SimpleDateFormat(expectedPattern);

        // change to simpler date only
        String targetPattern = "MMMM d, yyyy";
        SimpleDateFormat targetFormatter = new SimpleDateFormat(targetPattern);

        // unexpected timestamp format will cause parsing error
        try {
            Date date = expectedFormatter.parse(timestamp);
            String simpleDate= targetFormatter.format(date);
            return simpleDate;
        }
        catch (ParseException e) {
            Log.e("~~~~~~~~~~~~~~~~~~~", "Error formatting timestamp.", e);
            return timestamp;
        }

    }

}
