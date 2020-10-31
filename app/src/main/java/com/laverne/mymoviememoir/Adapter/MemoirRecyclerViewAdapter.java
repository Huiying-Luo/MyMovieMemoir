package com.laverne.mymoviememoir.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.mymoviememoir.Entity.Memoir;

import com.laverne.mymoviememoir.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MemoirRecyclerViewAdapter extends RecyclerView.Adapter<MemoirRecyclerViewAdapter.ViewHolder> {

    private List<Memoir> memoirs;
    private OnDetailsListener mOnDetailsListener;
    private boolean isPublicRating;


    public MemoirRecyclerViewAdapter(List<Memoir> memoirs, OnDetailsListener onDetailsListener, boolean isPublicRating) {
        this.memoirs = memoirs;
        mOnDetailsListener = onDetailsListener;
        this.isPublicRating = isPublicRating;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameTextView;
        public TextView releaseDateTextView;
        public TextView watchDateTextView;
        public TextView cineNameTextView;
        public TextView cinePostcodeTextView;
        public TextView commentTextView;
        public RatingBar ratingBar;
        public RatingBar publicRatingBar;
        public ImageView imageView;
        public boolean isPublicRating;
        public TextView publicRatingBarTv;

        public OnDetailsListener onDetailsListener;


        public ViewHolder(@NonNull View itemView, OnDetailsListener onDetailsListener, boolean isPublicRating) {
            super(itemView);
            this.onDetailsListener = onDetailsListener;
            this.isPublicRating = isPublicRating;

            nameTextView = itemView.findViewById(R.id.tv_memoir_movie_name);
            releaseDateTextView = itemView.findViewById(R.id.tv_memoir_release_date);
            watchDateTextView = itemView.findViewById(R.id.tv_memoir_watch_date);
            cineNameTextView = itemView.findViewById(R.id.tv_memoir_cinema_name);
            cinePostcodeTextView = itemView.findViewById(R.id.tv_memoir_cinema_postcode);
            commentTextView = itemView.findViewById(R.id.tv_memoir_comment);
            ratingBar = itemView.findViewById(R.id.memoir_rating_bar);
            publicRatingBar = itemView.findViewById(R.id.memoir_public_rating_bar);
            imageView = itemView.findViewById(R.id.memoir_image);
            publicRatingBarTv = itemView.findViewById(R.id.public_rb_tv);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onDetailsListener.onDetailsClick(getAdapterPosition());
        }
    }


    @Override
    public int getItemCount() {
        return memoirs.size();
    }


    @NonNull
    @Override
    public MemoirRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View memoirsView = inflater.inflate(R.layout.memoir_rv_layout, parent, false);
        // construct the viewholder with the new view
        MemoirRecyclerViewAdapter.ViewHolder viewHolder = new MemoirRecyclerViewAdapter.ViewHolder(memoirsView, mOnDetailsListener, isPublicRating);
        return viewHolder;
    }


    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull MemoirRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final Memoir memoir = memoirs.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(memoir.getMovieName());
        TextView tvReleaseDate = viewHolder.releaseDateTextView;
        tvReleaseDate.setText(memoir.getMovieReleasedate());
        TextView tvWatchDate = viewHolder.watchDateTextView;
        tvWatchDate.setText(memoir.getMemDatetime());
        TextView tvCinemaName = viewHolder.cineNameTextView;
        tvCinemaName.setText(memoir.getCineId().getCineName());
        TextView tvCinePostcode = viewHolder.cinePostcodeTextView;
        tvCinePostcode.setText(memoir.getCineId().getCinePostcode());
        TextView tvComment = viewHolder.commentTextView;
        tvComment.setText(memoir.getMemComment());
        RatingBar rb = viewHolder.ratingBar;
        rb.setRating((float) memoir.getMemRating());
        RatingBar rbPublic = viewHolder.publicRatingBar;
        TextView rbPublicTv = viewHolder.publicRatingBarTv;
        if (!viewHolder.isPublicRating) {
            rbPublic.setVisibility(View.INVISIBLE);
            rbPublicTv.setTextColor(Color.TRANSPARENT);
        } else {
            rbPublic.setRating((float) memoir.getPublicRating());
            rb.setAlpha((float) 0.4);
        }

        final ImageView imageV = viewHolder.imageView;
        String url = memoir.getImageSrc();
        // setImage
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageV);

    }


    public void updateList(List<Memoir> memoirs) {
        this.memoirs = memoirs;
        notifyDataSetChanged();
    }


    // create a new interface to detect the click on the recycler itemview
    public interface OnDetailsListener {
        void onDetailsClick(int position);
    }
}
