package com.laverne.mymoviememoir.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.laverne.mymoviememoir.DAO.WatchlistDAO;
import com.laverne.mymoviememoir.Database.WatchlistDatabase;
import com.laverne.mymoviememoir.Entity.Watchlist;

import java.util.List;

public class WatchlistRepository {

    private WatchlistDAO dao;
    private LiveData<List<Watchlist>> allWatchlists;
    private Watchlist watchlist;

    public WatchlistRepository(Application application) {
        WatchlistDatabase db = WatchlistDatabase.getInstance(application);
        dao = db.watchlistDao();
    }


    public LiveData<List<Watchlist>> getAllWatchlists(final String id) {
        allWatchlists = dao.getAllByUserId(id);
        return allWatchlists;
    }


    public void insert(final Watchlist watchlist) {
        WatchlistDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insert(watchlist);
            }
        });
    }


    public void delete(final Watchlist watchlist) {
        WatchlistDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(watchlist);
            }
        });
    }


    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }
}
