package com.laverne.mymoviememoir.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.mymoviememoir.APIManager;
import com.laverne.mymoviememoir.Adapter.SearchRecyclerViewAdapter;
import com.laverne.mymoviememoir.Model.MovieBreif;
import com.laverne.mymoviememoir.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private EditText editText;
    private Button searchBtn;
    private TextView resultTitleTextView;
    private TextView noResultTextView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private SearchRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<MovieBreif> movieBreifs;

    public SearchFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        configureUI(view);
        initRecyclerView();
        setUpSearchBtn();

        return view;
    }


    private void configureUI(View view) {
        editText = view.findViewById(R.id.et_search);
        searchBtn = view.findViewById(R.id.btn_search);
        resultTitleTextView = view.findViewById(R.id.search_result_tv);
        noResultTextView = view.findViewById(R.id.no_result_tv);
        recyclerView = view.findViewById(R.id.search_recycler_view);

        configureEditText();
    }


    private void configureEditText() {
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }


    private void initRecyclerView() {
        movieBreifs = new ArrayList<>();
        adapter = new SearchRecyclerViewAdapter(movieBreifs);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }


    private void setUpSearchBtn() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String keyword = editText.getText().toString().toLowerCase().trim();

                new SearchMovieTask().execute(keyword);
            }
        });
    }


    private void getInfoList(String result) {
        movieBreifs.removeAll(movieBreifs);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("Search");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    MovieBreif movieBreif = null;
                    String title = jsonArray.getJSONObject(i).getString("Title").trim();
                    String year = jsonArray.getJSONObject(i).getString("Year").trim();
                    String src = jsonArray.getJSONObject(i).getString("Poster");
                    String imdbId = jsonArray.getJSONObject(i).getString("imdbID");
                    movieBreif = new MovieBreif(title, year, src, imdbId);
                    movieBreifs.add(movieBreif);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //info = "NO INFO FOUND";
        }
    }


    private class SearchMovieTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return APIManager.OMDbAPI.search(params[0], new String[]{"type"}, new String[]{"movie"});
        }

        @Override
        protected void onPostExecute(String result) {
            getInfoList(result);
            resultTitleTextView.setText("Search Result");
            if (movieBreifs.size() != 0) {
                noResultTextView.setText("");
            } else {
                noResultTextView.setText("NO INFO FOUND");
            }
            // display search results
            adapter.updateList(movieBreifs);
        }
    }
}
