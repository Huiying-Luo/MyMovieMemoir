package com.laverne.mymoviememoir.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.mymoviememoir.Adapter.HomeRecyclerViewAdapter;
import com.laverne.mymoviememoir.Model.TopMovie;
import com.laverne.mymoviememoir.NetworkConnection.NetworkConnection;
import com.laverne.mymoviememoir.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView userTextView;
    private TextView dateTextView;
    private TextView titleTextView;
    private CardView cardView;
    private ImageView imageView;
    private TextView title1TextView;
    private TextView title2TextView;
    private TextView title3TextView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private HomeRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private String firstName;
    private String userId;

    private List<TopMovie> topMovies;

    private NetworkConnection networkConnection;


    public HomeFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getActivity().setTitle("Home");

        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        configureUI(view);
        getUserInfoFromSharedPref();
        setCurrentDate();

        networkConnection = new NetworkConnection();
        new GetTopFiveMoviesTask().execute(userId);

        return view;
    }


    private void configureUI(View view) {
        userTextView = view.findViewById(R.id.welcome_tv);
        dateTextView = view.findViewById(R.id.date_tv);
        titleTextView = view.findViewById(R.id.top_five_tv);
        cardView = view.findViewById(R.id.cardView);
        imageView = view.findViewById(R.id.imageView);
        recyclerView = view.findViewById(R.id.home_recycler_view);
        title1TextView = view.findViewById(R.id.home_title1);
        title2TextView = view.findViewById(R.id.home_title2);
        title3TextView = view.findViewById(R.id.home_title3);

        title1TextView.setText("Moive Name");
        title2TextView.setText("Release Date");
        title3TextView.setText("Rating Score");
    }


    private void getUserInfoFromSharedPref() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        firstName = sharedPref.getString("firstName", null);
        userId = sharedPref.getString("userId", null);
        userTextView.setText("  Welcome, " + firstName);
    }


    private void setCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(today);
        dateTextView.setText("  " + dateStr);
    }

    private class text extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    private class GetTopFiveMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... parmas) {
            return networkConnection.getTopFiveMovies(Integer.parseInt(parmas[0]));
        }

        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(String topMovieList) {
            topMovies = new ArrayList<TopMovie>();
            if (topMovieList != null && topMovieList.length() != 0) {
                try {
                    JSONArray jsonArray = new JSONArray(topMovieList);
                    if (jsonArray.length() == 0) {
                        title1TextView.setText("");
                        title2TextView.setText("No Records...");
                        title3TextView.setText("");
                        return;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        TopMovie movie = new TopMovie(jsonObject.getString("MovieName"),
                                                      jsonObject.getString("ReleaseDate"),
                                                      jsonObject.getDouble("RatingScore"));
                        topMovies.add(movie);
                    }

                    adapter = new HomeRecyclerViewAdapter(topMovies);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                    recyclerView.setAdapter(adapter);

                    layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("Error","network error");
            }
        }
    }
}