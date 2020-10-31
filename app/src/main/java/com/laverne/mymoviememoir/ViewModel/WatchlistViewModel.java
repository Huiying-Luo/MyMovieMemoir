package com.laverne.mymoviememoir.ViewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.laverne.mymoviememoir.Entity.Watchlist;
import com.laverne.mymoviememoir.Repository.WatchlistRepository;
import com.laverne.mymoviememoir.Utilities;

import java.util.ArrayList;
import java.util.List;

public class WatchlistViewModel extends ViewModel {

    private WatchlistRepository wRepository;
    private MutableLiveData<List<Watchlist>> allWatchlists;

    public WatchlistViewModel() {
        allWatchlists = new MutableLiveData<>();
    }

    public void setWatchlists(List<Watchlist> watchlists) {
        allWatchlists.setValue(watchlists);
    }

    public void initializeVars(Application application) {
        wRepository = new WatchlistRepository(application);
    }

    public LiveData<List<Watchlist>> getAllWatchlists(String id) {
        return wRepository.getAllWatchlists(id);
    }

    public void insert(Watchlist watchlist) {
        wRepository.insert(watchlist);
    }

    public void delete(Watchlist watchlist) {
        wRepository.delete(watchlist);
    }

}
