package com.example.android.popularmovies.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    public static final String YT_THUMB_URL = "http://img.youtube.com/vi/";
    Picasso picasso;
    private Context context;
    private List<Trailer> trailersList;
    private TrailerAdapterListener trailerAdapterListener;

    public TrailerAdapter(Context context, List<Trailer> trailers) {
        this.context = context;
        this.trailersList = trailers;
        this.trailerAdapterListener = (TrailerAdapterListener) context;
    }

    public String makeThumbnailURL(String thumbnailId) {
        return YT_THUMB_URL.concat(thumbnailId).concat("/0.jpg");
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForReviewItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForReviewItem, parent, shouldAttachToParentImmediately);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, final int position) {
        holder.title.setText(trailersList.get(position).getName());

        String thumbnailURL = makeThumbnailURL(trailersList.get(position).getKey());
        Picasso.with(context)
                .load(thumbnailURL)
                .placeholder(R.drawable.load)
                .into(holder.imageView);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trailerAdapterListener.onVideoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailersList.size();
    }

    public Trailer getTrailerFromPosition(int position) {
        return trailersList.get(position);
    }

    public interface TrailerAdapterListener {
        void onVideoClick(int position);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView imageView;
        private FrameLayout container;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.thumbnail_trailer);
            container = itemView.findViewById(R.id.container);
        }
    }
}