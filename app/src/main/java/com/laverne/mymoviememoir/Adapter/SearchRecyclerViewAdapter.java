package com.laverne.mymoviememoir.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.mymoviememoir.Model.MovieBreif;
import com.laverne.mymoviememoir.MovieViewActivity;
import com.laverne.mymoviememoir.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>  {

    private List<MovieBreif> movieBreifs;
    private Context context;


    public SearchRecyclerViewAdapter(List<MovieBreif> movieBreifs) {
        this.movieBreifs = movieBreifs;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView titleTextView;
        public TextView yearTextView;
        public ImageView imageView;
        public Button detailBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.tv_movie_brief);
            imageView = itemView.findViewById(R.id.search_image);
            yearTextView = itemView.findViewById(R.id.tv_release_year);
            detailBtn = itemView.findViewById(R.id.dtails_btn);

            context = itemView.getContext();
        }
    }


    @Override
    public int getItemCount() {
        return movieBreifs.size();
    }


    @NonNull
    @Override
    public SearchRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View moviesView = inflater.inflate(R.layout.search_rv_layout, parent, false);
        // construct the viewholder with the new view
        ViewHolder viewHolder = new ViewHolder(moviesView);
        return viewHolder;
    }


    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final MovieBreif movieBreif = movieBreifs.get(position);
        // viewholder binding with its data at the specified position
        TextView tvTitle = viewHolder.titleTextView;
        tvTitle.setText(movieBreif.getTitle());
        TextView tvYear = viewHolder.yearTextView;
        tvYear.setText("Release Year: " + movieBreif.getYear());
        final ImageView imageV = viewHolder.imageView;
        String url = movieBreif.getImageSrc();
        // setImage
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageV);
        Button btn = viewHolder.detailBtn;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE);
                String userId = sharedPref.getString("userId", null);

                Intent intent = new Intent(context, MovieViewActivity.class);
                intent.putExtra("movieName", movieBreif.getTitle());
                intent.putExtra("userId", userId);
                intent.putExtra("from", "search");
                context.startActivity(intent);
            }
        });
    }


    public void updateList(List<MovieBreif> movieBreifs) {
        this.movieBreifs = movieBreifs;
        notifyDataSetChanged();
    }
}
