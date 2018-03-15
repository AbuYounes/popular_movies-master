package com.example.android.popularmovies.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Review> reviewsList;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviewsList = reviews;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewsList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForReviewItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForReviewItem, parent, shouldAttachToParentImmediately);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.authorText.setText(reviewsList.get(position).getAuthor());
        holder.reviewText.setText(reviewsList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView authorText;
        private TextView reviewText;

        ReviewViewHolder(View itemView) {
            super(itemView);
            authorText = itemView.findViewById(R.id.review_author);
            reviewText = itemView.findViewById(R.id.review_content);
        }
    }
}