package com.laverne.mymoviememoir.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.mymoviememoir.APIManager;
import com.laverne.mymoviememoir.Adapter.MemoirRecyclerViewAdapter;
import com.laverne.mymoviememoir.Entity.Memoir;
import com.laverne.mymoviememoir.MovieViewActivity;
import com.laverne.mymoviememoir.NetworkConnection.NetworkConnection;
import com.laverne.mymoviememoir.R;
import com.laverne.mymoviememoir.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemoirFragment extends Fragment implements MemoirRecyclerViewAdapter.OnDetailsListener {
    private TextView noMemoirTextView;
    private Spinner sortSpinner;
    private Spinner filterSpinner;
    private ArrayAdapter<String> sortSpinnerAdapter;
    private ArrayAdapter<String> filterSpinnerAdapter;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private MemoirRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Memoir> memoirs;
    private List<Memoir> allMemoirs;
    private List<Memoir> defaultMemoirs;

    private boolean isPublicRating = false;
    private boolean isLastMemoir = false;
    private boolean firstLoaded = false;
    private String userId;

    private NetworkConnection networkConnection;


    public MemoirFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.memoir_fragment, container, false);

        getUserIdFromSharedPref();
        configureUI(view);
        configureSortSpinner();
        configureFilterSpinner();

        networkConnection = new NetworkConnection();
        new GetAllMemoirTask().execute();

        return view;
    }


    private void configureUI(View view) {
        noMemoirTextView = view.findViewById(R.id.no_memoir_tv);
        recyclerView = view.findViewById(R.id.memoir_recycler_view);
        sortSpinner = view.findViewById(R.id.memoir_sort_spinner);
        filterSpinner = view.findViewById(R.id.memoir_filter_spinner);
    }


    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MemoirRecyclerViewAdapter(memoirs, this, isPublicRating);

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
    }


    private void filterByGenre(String genre) {
        memoirs.removeAll(memoirs);
        if (!genre.equals("All")) {
            for (int i = 0; i < allMemoirs.size(); i++) {
                Memoir tempMemoir = allMemoirs.get(i);
                String tempGenres = tempMemoir.getGenre().replace(", ", ",");
                String[] genreList = tempGenres.split(",");
                List<String> arraylist = new ArrayList<>();
                arraylist = Arrays.asList(genreList);
                if (arraylist.contains(genre)) {
                    memoirs.add(tempMemoir);
                }
            }
        } else {
            memoirs.addAll(allMemoirs);
        }
        if (memoirs.isEmpty()) {
            noMemoirTextView.setText("You haven't watched any " + genre + " movies...");
        } else {
            noMemoirTextView.setText("");
        }
        adapter.updateList(memoirs);
        layoutManager.scrollToPosition(0);
    }


    private void sortByWatchDate() {
        Collections.sort(memoirs, new Comparator<Memoir>() {
            @Override
            public int compare(Memoir o1, Memoir o2) {
                return o2.getMemDatetime().compareTo(o1.getMemDatetime());
            }
        });
        // in case of filtering after sorting
        Collections.sort(allMemoirs, new Comparator<Memoir>() {
            @Override
            public int compare(Memoir o1, Memoir o2) {
                return o2.getMemDatetime().compareTo(o1.getMemDatetime());
            }
        });
        updateRecyclerViewAdapter();
    }


    private void sortByUserRating() {
        Collections.sort(memoirs, new Comparator<Memoir>() {
            @Override
            public int compare(Memoir o1, Memoir o2) {
                return Double.compare(o2.getMemRating(), o1.getMemRating());
            }
        });
        Collections.sort(allMemoirs, new Comparator<Memoir>() {
            @Override
            public int compare(Memoir o1, Memoir o2) {
                return Double.compare(o2.getMemRating(), o1.getMemRating());
            }
        });
        updateRecyclerViewAdapter();
    }


    private void sortByPublicRating() {
        Collections.sort(memoirs, new Comparator<Memoir>() {
            @Override
            public int compare(Memoir o1, Memoir o2) {
                return Double.compare(o2.getPublicRating(), o1.getPublicRating());
            }
        });
        Collections.sort(allMemoirs, new Comparator<Memoir>() {
            @Override
            public int compare(Memoir o1, Memoir o2) {
                return Double.compare(o2.getPublicRating(), o1.getPublicRating());
            }
        });
        if (isPublicRating) {
            adapter.updateList(memoirs);
            layoutManager.scrollToPosition(0);
        } else {
            isPublicRating = true;
            // display public rating bar
            adapter = new MemoirRecyclerViewAdapter(memoirs, this, isPublicRating);
            recyclerView.setAdapter(adapter);
        }

    }


    private void updateRecyclerViewAdapter() {
        if (!isPublicRating) {
            adapter.updateList(memoirs);
            layoutManager.scrollToPosition(0);
        } else {
            isPublicRating = false;
            adapter = new MemoirRecyclerViewAdapter(memoirs, this, isPublicRating);
            recyclerView.setAdapter(adapter);
        }
    }


    private class GetAllMemoirTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return networkConnection.getMemoirsByUserId(Integer.parseInt(userId));
        }

        @Override
        protected void onPostExecute(String result) {
            memoirs = new ArrayList<>();
            allMemoirs = new ArrayList<>();
            defaultMemoirs = new ArrayList<>();
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() == 0) {
                        noMemoirTextView.setText("You haven't added any memoirs so far...");
                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (i == jsonArray.length() - 1) {
                                isLastMemoir = true;
                            }
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String movieName = jsonObject.getString("movieName");
                            String releaseDate = jsonObject.getString("movieReleaseDate");
                            releaseDate = releaseDate.substring(0, 10);
                            String watchDate = jsonObject.getString("memDatetime");
                            watchDate = watchDate.substring(0, 10);
                            Double rating = jsonObject.getDouble("memRating");
                            JSONObject jsonObject1 = jsonObject.getJSONObject("cineId");
                            String cineName = jsonObject1.getString("cineName");
                            String cinePostcode = jsonObject1.getString("cinePostcode");
                            String comment = jsonObject.getString("memComment");
                            String[] details = {movieName, releaseDate, watchDate, String.valueOf(rating), comment, cineName, cinePostcode};

                            // search based on movie name and release year first, preventing duplicate of remake
                            new SearchMovieWithYearAndAddMemoirTask().execute(details);
                        }
                    }
                } catch (JSONException e) {
                    Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
                    e.printStackTrace();
                }
            } else {
                Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
            }
        }
    }


    private class SearchMovieWithYearAndAddMemoirTask extends AsyncTask<String, Void, String> {

        Memoir tempMemoir;
        String[] memoirDetails;

        @Override
        protected String doInBackground(String... details) {
            memoirDetails = details;
            Double score = Double.parseDouble(details[3]);
            tempMemoir = new Memoir(details[0], details[1], details[2], score, details[4]);
            tempMemoir.setCineId(details[5], details[6]);
            return APIManager.OMDbAPI.searchByName(details[0], new String[]{"type", "y"}, new String[]{"movie", details[1].substring(0, 4)});
        }

        @Override
        protected void onPostExecute(String result) {
            getInfoFromSearch(result, tempMemoir, memoirDetails, true);
        }
    }


    private class SearchMovieAndAddMemoirTask extends AsyncTask<String, Void, String> {

        Memoir tempMemoir;
        String[] memoirDetails;

        @Override
        protected String doInBackground(String... details) {
            memoirDetails = details;
            Double score = Double.parseDouble(details[3]);
            tempMemoir = new Memoir(details[0], details[1], details[2], score, details[4]);
            tempMemoir.setCineId(details[5], details[6]);
            return APIManager.OMDbAPI.searchByName(details[0], new String[]{"type"}, new String[]{"movie"});
        }

        @Override
        protected void onPostExecute(String result) {
            getInfoFromSearch(result, tempMemoir, memoirDetails, false);
        }
    }


    private void getInfoFromSearch(String result, Memoir tempMemoir, String[] memoirDetails, boolean isWithYear) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("Response").equals("False")) {
                // in the IMDB database, the year of the release date and the release year are different
                // so adding the year as parameter may not find the movie
                if (isWithYear) {
                    new SearchMovieAndAddMemoirTask().execute(memoirDetails);
                }
            } else {
                String imageSrc = jsonObject.getString("Poster");
                String genre = jsonObject.getString("Genre").trim();
                String r = jsonObject.getString("imdbRating").trim();
                Double publicRating = Double.parseDouble(r) / 2;
                tempMemoir.setImageSrc(imageSrc);
                tempMemoir.setGenre(genre);
                tempMemoir.setPublicRating(publicRating);
                memoirs.add(tempMemoir);
                allMemoirs.add(tempMemoir);
                defaultMemoirs.add(tempMemoir);
                // retrieve all memoirs, set up recycler view
                if (isLastMemoir) {
                    initRecyclerView();
                    // first loaded finish
                    firstLoaded = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //info = "NO INFO FOUND";
        }
    }


    private void configureSortSpinner() {
        String[] options = new String[]{"Default", "Watch Date: new to old", "Your Score: high to low", "Public Score: high to low"};

        final List<String> sortList = new ArrayList<String>(Arrays.asList(options));

        sortSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, sortList);
        sortSpinner.setAdapter(sortSpinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // spinner will be setup before retrieve memoir complete
                        if (firstLoaded) {
                            adapter.updateList(defaultMemoirs);
                        }
                        break;
                    case 1:
                        sortByWatchDate();
                        break;
                    case 2:
                        sortByUserRating();
                        break;
                    case 3:
                        sortByPublicRating();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void configureFilterSpinner() {
        String[] genres = new String[]{"Select a Genre", "All", "Action", "Action-Comedy", "Adventure", "Animation", "Biography", "Comedy",
                "Comedy-Romance", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Film Noir", "History", "Horror", "Music", "Musical",
                "Mystery", "Romance", "Sci-Fi", "Short Film", "Sport", "Superhero", "Thriller", "War", "Western"};

        final List<String> genrelist = new ArrayList<String>(Arrays.asList(genres));

        filterSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, genrelist) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        filterSpinner.setAdapter(filterSpinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    filterByGenre(genrelist.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void getUserIdFromSharedPref() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        userId = sharedPref.getString("userId", null);
    }


    // interface method
    // click recylcer view item
    @Override
    public void onDetailsClick(int position) {
        Intent intent = new Intent(getActivity(), MovieViewActivity.class);
        intent.putExtra("movieName", memoirs.get(position).getMovieName());
        intent.putExtra("userId", userId);
        intent.putExtra("from", "memoir");
        startActivity(intent);
    }
}
