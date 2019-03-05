package com.example.pavlyk.cafefinder.customViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.pavlyk.cafefinder.R;

/**
 * Created by Kubik on 12/8/16.
 */

public class ReviewView extends LinearLayout {

    private TextView mTvName;
    private TextView mTvReview;
    private RatingBar mRbRating;

    public ReviewView(Context context) {
        super(context);

        initControls(context);
    }

    private void initControls(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.review_view, this);

        mTvName = (TextView) findViewById(R.id.tv_reviewer_name);
        mTvReview = (TextView) findViewById(R.id.tv_review);
        mRbRating = (RatingBar) findViewById(R.id.rb_reviewer_rating);

    }

    public void setComponents(String reviewerName, float rating, String review) {
        mTvName.setText(reviewerName);
        mRbRating.setRating(rating);
        mTvReview.setText(review);
    }


}
