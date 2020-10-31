package com.laverne.mymoviememoir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.mymoviememoir.Model.TopMovie;
import com.laverne.mymoviememoir.R;

import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView dateTextView;
        private RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.tv_movie_name);
            dateTextView = itemView.findViewById(R.id.tv_release_date);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }


    @Override
    public int getItemCount() {
        return 5;
    }

    private List<TopMovie> topMovies;

    public HomeRecyclerViewAdapter(List<TopMovie> topMovies) {
        this.topMovies = topMovies;
    }


    @NonNull
    @Override
    public HomeRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View moviesView = inflater.inflate(R.layout.home_rv_layout, parent, false);
        // construct the viewholder with the new view
        ViewHolder viewHolder = new ViewHolder(moviesView);
        return viewHolder;
    }


    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final TopMovie movie = topMovies.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(movie.getName());
        TextView tvDate =viewHolder.dateTextView;
        tvDate.setText(movie.getDate());
        RatingBar ratingB = viewHolder.ratingBar;
        ratingB.setRating(movie.getScore().floatValue());
    }
}
