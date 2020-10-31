package com.laverne.mymoviememoir.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.laverne.mymoviememoir.Adapter.WatchlistRecyclerViewAdapter;
import com.laverne.mymoviememoir.Entity.Watchlist;
import com.laverne.mymoviememoir.R;
import com.laverne.mymoviememoir.Utilities;
import com.laverne.mymoviememoir.ViewModel.WatchlistViewModel;

import java.util.ArrayList;
import java.util.List;

public class WatchlistFragment extends Fragment implements WatchlistRecyclerViewAdapter.OnDeleteListener {

    private RecyclerView recyclerView;
    private TextView noListTextView;
    private RecyclerView.Adapter mAdapter;
    private WatchlistRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private WatchlistViewModel watchlistViewModel;

    private boolean firstLoad = true;
    private List<Watchlist> watchlistList;

    public WatchlistFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.watchlist_fragment, container, false);

        configureUI(view);

        initializeRecyclerView();

        // get all watchlist from room database
        getAllWatchlistFromRoomDatabase(getUserIdFromSharedPref());

/*
        // get all watchlists from firestore
        getAllWatchlistFromFirestore(getUserIdFromSharedPref());
*/


        return view;
    }


    private void configureUI(View view) {
        noListTextView = view.findViewById(R.id.no_wathchlist_tv);
        recyclerView = view.findViewById(R.id.watchlist_recycler_view);
    }


    private void initializeRecyclerView() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WatchlistRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
    }


    private String getUserIdFromSharedPref() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        return sharedPref.getString("userId", null);
    }


    @Override
    public void onDeleteClick(Watchlist watchlist) {

        // delete watchlist from Room database
        watchlistViewModel.delete(watchlist);
        Toast.makeText(getActivity(), "Record has been deleted.", Toast.LENGTH_SHORT).show();

/*
        // delete watchlist from firestore
        deleteWatchlistFromFiresotre(watchlist);
 */


    }


    private void configureNoResultTextView(List<Watchlist> watchlistList) {
        if (watchlistList.isEmpty()) {
            noListTextView.setText("Empty Watchlist...");
        } else {
            noListTextView.setText("");
        }
    }


    private void getAllWatchlistFromRoomDatabase(String userId) {
        // initialize view model
        watchlistViewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        watchlistViewModel.initializeVars(getActivity().getApplication());

        watchlistViewModel.getAllWatchlists(userId).observe(getViewLifecycleOwner(), new Observer<List<Watchlist>>() {
            @Override
            public void onChanged(List<Watchlist> watchlists) {
                configureNoResultTextView(watchlists);
                adapter.setWatchlists(watchlists);
            }
        });
    }


    private void getAllWatchlistFromFirestore(String userId) {
        watchlistList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Watchlist")
                .whereEqualTo("userId", userId)
                // listen for real-time update, speed up query
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
                            return;
                        }
                        if (firstLoad) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.get("userId") != null && doc.get("movieName") != null && doc.get("releaseDate") != null && doc.get("addDateTime") != null) {
                                    Watchlist watchlist = new Watchlist(doc.getString("userId"), doc.getString("movieName"), doc.getString("releaseDate"), doc.getString("addDateTime"));
                                    watchlistList.add(watchlist);
                                } else {
                                    Log.i("firebase", "firebase data not correct");
                                }
                            }
                            // in case of watchlist is empty
                            configureNoResultTextView(watchlistList);
                            adapter.setWatchlists(watchlistList);
                            firstLoad = false;
                        } else {
                            // not first time to load this screen, no need to add the wathchlist from firestore again.
                            // this screen only can delete watchlist
                            configureNoResultTextView(watchlistList);
                            adapter.updateList(watchlistList);
                        }
                    }
                });
    }


    private void deleteWatchlistFromFiresotre(final Watchlist watchlist) {
        final String[] documentId = {null};
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        // query the document id of this watchlist first
        db.collection("Watchlist")
                .whereEqualTo("userId", watchlist.getUserId())
                .whereEqualTo("movieName", watchlist.getMovieName())
                .whereEqualTo("releaseDate", watchlist.getReleaseDate())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                documentId[0] = document.getId();
                            }
                            db.collection("Watchlist").document(documentId[0])
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            watchlistList.remove(watchlist);
                                            Toast.makeText(getActivity(), "Record has been deleted.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
