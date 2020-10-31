package com.laverne.mymoviememoir.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.mymoviememoir.Entity.Watchlist;
import com.laverne.mymoviememoir.MovieViewActivity;
import com.laverne.mymoviememoir.R;

import java.util.ArrayList;
import java.util.List;

public class WatchlistRecyclerViewAdapter extends RecyclerView.Adapter<WatchlistRecyclerViewAdapter.ViewHolder> {

    private List<Watchlist> watchlists;
    private Context context;
    private OnDeleteListener onDeleteListener;


    //default constructor
    public WatchlistRecyclerViewAdapter(OnDeleteListener onDeleteListener) {
        watchlists = new ArrayList<Watchlist>();
        this.onDeleteListener = onDeleteListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView releaseDateTextView;
        public TextView addDatetimeTextView;
        public Button viewBtn;
        public Button deleteBtn;
        OnDeleteListener onDeleteListener;


        public ViewHolder(@NonNull View itemView, OnDeleteListener onDeleteListener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.watchlist_name_tv);
            releaseDateTextView = itemView.findViewById(R.id.watchlist_release_tv);
            addDatetimeTextView = itemView.findViewById(R.id.watchlist_date_tv);
            viewBtn = itemView.findViewById(R.id.btn_view_watchlist);
            deleteBtn = itemView.findViewById(R.id.btn_delete_watchlist);
            this.onDeleteListener = onDeleteListener;

            context = itemView.getContext();
        }
    }


    @NonNull
    @Override
    public WatchlistRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the view from XML layout file
        View watchlistView = inflater.inflate(R.layout.watchlist_rv_layout, parent, false);
        // construct the viewholder with the new view
        ViewHolder viewHolder = new ViewHolder(watchlistView, onDeleteListener);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final WatchlistRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final Watchlist watchlist = watchlists.get(position);
        // viewholder binding with its data at the specified position
        TextView tvName = viewHolder.nameTextView;
        tvName.setText(watchlist.getMovieName());
        TextView tvRelease = viewHolder.releaseDateTextView;
        tvRelease.setText("Release Date: " + watchlist.getReleaseDate());
        TextView tvDatetime = viewHolder.addDatetimeTextView;
        tvDatetime.setText("Added on: " + watchlist.getAddDateTime());

        Button btnView = viewHolder.viewBtn;
        Button btnDelete = viewHolder.deleteBtn;

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences("User", Context.MODE_PRIVATE);
                String userId = sharedPref.getString("userId", null);
                Intent intent = new Intent(context, MovieViewActivity.class);
                intent.putExtra("movieName", watchlist.getMovieName());
                intent.putExtra("userId", userId);
                intent.putExtra("from", "watchlist");
                context.startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a alert dialog let user confirm deletion
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Delete");
                alert.setMessage("Do you want to remove \"" + watchlist.getMovieName() +"\" from watchlist?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        watchlists.remove(watchlist);
                        notifyDataSetChanged();
                        onDeleteListener.onDeleteClick(watchlist);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                alert.create().show();
            }
        });
    }


    public void setWatchlists(List<Watchlist> watchlists) {
        this.watchlists = watchlists;
        notifyDataSetChanged();
    }


    public void updateList(List<Watchlist> watchlists) {
        this.watchlists = watchlists;
        notifyDataSetChanged();
    }


// create a new interface to detect the click on delete button
    public interface OnDeleteListener{
        void onDeleteClick(Watchlist watchlist);
    }


    @Override
    public int getItemCount() {
        return watchlists.size();
    }
}
