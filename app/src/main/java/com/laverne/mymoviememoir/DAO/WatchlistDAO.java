package com.laverne.mymoviememoir.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.laverne.mymoviememoir.Entity.Watchlist;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WatchlistDAO {

    @Query("SELECT * FROM watchlist WHERE user_id = :id")
    LiveData<List<Watchlist>> getAllByUserId(String id);

    @Query("SELECT * FROM watchlist WHERE user_id = :id AND movie_name = :name AND release_date = :releaseDate LIMIT 1")
    Watchlist findByNameAndRelease(String id, String name, String releaseDate);

    @Insert(onConflict = IGNORE)
    long insert(Watchlist watchlist);

    @Delete
    void delete(Watchlist watchlist);
}
