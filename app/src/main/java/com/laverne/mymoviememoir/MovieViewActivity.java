package com.laverne.mymoviememoir;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.laverne.mymoviememoir.Database.WatchlistDatabase;
import com.laverne.mymoviememoir.Entity.Watchlist;
import com.laverne.mymoviememoir.ViewModel.WatchlistViewModel;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MovieViewActivity extends AppCompatActivity {
    private final static int REQUEST_CODE = 666;
    private TextView titleTextView;
    private TextView genreTextView;
    private TextView castTextView;
    private TextView releaseDateTextView;
    private TextView countryTextView;
    private TextView directorTextView;
    private TextView storylineTextView;
    private RatingBar ratingBar;
    private Button watchlistBtn;
    private Button memoirBtn;

    private String movieName = null;
    private String releaseDate = null;
    private String currentDatetime = null;
    private String imageSrc = null;
    private String userId;
    private String fromActivity;

    private boolean hasClickAddWatchlist = false;

    private WatchlistDatabase db = null;
    private WatchlistViewModel watchlistViewModel;

    private FirebaseFirestore fdb;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_view);

        configureUI();
        getInfoFromIntent();

        // Search Info of movie
        new SearchTask().execute(movieName);

        //initialize firestore
        fdb = FirebaseFirestore.getInstance();

        initialWatchlistDB();

        configureWatchlistButton();
        configureMemoirButton();
    }


    private void configureUI() {
        setTitle("Movie Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleTextView = findViewById(R.id.movie_title_tv);
        genreTextView = findViewById(R.id.genre_tv);
        castTextView = findViewById(R.id.cast_tv);
        releaseDateTextView = findViewById(R.id.release_tv);
        countryTextView = findViewById(R.id.country_tv);
        directorTextView = findViewById(R.id.director_tv);
        storylineTextView = findViewById(R.id.storyline_tv);
        ratingBar = findViewById(R.id.public_rating_bar);

        watchlistBtn = findViewById(R.id.btn_add_watchlist);
        memoirBtn = findViewById(R.id.btn_add_memoir);
    }


    private void initialWatchlistDB() {
        db = WatchlistDatabase.getInstance(this);
        watchlistViewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        watchlistViewModel.initializeVars(getApplication());
    }


    private void getInfoFromIntent() {
        Intent intent = getIntent();
        movieName = intent.getStringExtra("movieName");
        userId = intent.getStringExtra("userId");
        fromActivity = intent.getStringExtra("from");
        titleTextView.setText(movieName);
    }


    private void configureWatchlistButton() {
        if (fromActivity.equals("watchlist")) {
            watchlistBtn.setEnabled(false);
            watchlistBtn.setBackgroundResource(R.drawable.button_disable);
        } else if (fromActivity.equals("memoir")) {
            watchlistBtn.setVisibility(View.GONE);
        } else {
            watchlistBtn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    if (hasClickAddWatchlist) {
                        Utilities.showAlertDialogwithOkButton(MovieViewActivity.this, "Alert", "This movie is already in Watchlist.");
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        Date today = calendar.getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        currentDatetime = dateFormat.format(today);
                        Watchlist watchlist = new Watchlist(userId, movieName, releaseDate, currentDatetime);

                        // add to watchilist database
                        new AddtoRoomDatabaseTask().execute(watchlist);

/*
                        // add to Firestore
                        checkAndAddToFirestore(watchlist);

 */
                    }
                }
            });
        }
    }


    private void configureMemoirButton() {
        if (fromActivity.equals("memoir")) {
            memoirBtn.setVisibility(View.GONE);
        } else {
            memoirBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieViewActivity.this, CreateMemoirActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("movieName", movieName);
                    bundle.putString("releaseDate", releaseDate);
                    bundle.putString("imageSrc", imageSrc);
                    bundle.putString("userId", userId);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private class SearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return APIManager.OMDbAPI.searchByName(params[0], new String[]{"type", "plot"}, new String[]{"movie", "full"});
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                imageSrc = jsonObject.getString("Poster");
                genreTextView.setText(jsonObject.getString("Genre"));
                castTextView.setText(jsonObject.getString("Actors"));
                countryTextView.setText(jsonObject.getString("Country"));
                directorTextView.setText(jsonObject.getString("Director"));
                storylineTextView.setText(jsonObject.getString("Plot"));
                String Score = jsonObject.getString("imdbRating");
                ratingBar.setRating(Float.valueOf(Score) / 2);

                releaseDate = jsonObject.getString("Released").trim();
                releaseDate = releaseDate.replace(" ", "-");
                // convert Date format
                SimpleDateFormat orginalFormat = new SimpleDateFormat("dd-MMM-yyyy");
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = orginalFormat.parse(releaseDate);
                releaseDate = newFormat.format(date);
                releaseDateTextView.setText(releaseDate);

            } catch (Exception e) {
                e.printStackTrace();
                //info = "NO INFO FOUND";
            }
        }
    }


    private class AddtoRoomDatabaseTask extends AsyncTask<Watchlist, Void, Long> {

        @Override
        protected Long doInBackground(Watchlist... watchlists) {
            long id = db.watchlistDao().insert(watchlists[0]);
            return id;
        }

        @Override
        protected void onPostExecute(Long id) {
            if (id == -1) {
                Utilities.showAlertDialogwithOkButton(MovieViewActivity.this, "Alert", "This movie is already in Watchlist.");
            } else {
                Utilities.showAlertDialogwithOkButton(MovieViewActivity.this, "Success", "Add to Watchlist Successfully!");
                hasClickAddWatchlist = true;
            }
        }
    }


    private void checkAndAddToFirestore(final Watchlist watchlist) {
        fdb.collection("Watchlist").whereEqualTo("movieName", movieName)
                .whereEqualTo("releaseDate", releaseDate)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // add to firestore
                                addToFirestore(watchlist);
                            } else {
                                Utilities.showAlertDialogwithOkButton(MovieViewActivity.this, "Alert", "This movie is already in Watchlist.");
                            }
                        } else {
                            Utilities.showAlertDialogwithOkButton(MovieViewActivity.this, "Error", "Something went wrong, please try again later.");
                        }
                    }
                });
    }


    private void addToFirestore(Watchlist watchlist) {
        fdb.collection("Watchlist").document().set(watchlist).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utilities.showAlertDialogwithOkButton(MovieViewActivity.this, "Success", "Add to Watchlist Successfully!");
                hasClickAddWatchlist = true;
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utilities.showAlertDialogwithOkButton(MovieViewActivity.this, "Error", "Something went wrong, please try again later.");
                    }
                });
    }
}
