package com.laverne.mymoviememoir.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"user_id", "movie_name","release_date"})
public class Watchlist {

    @ColumnInfo(name = "movie_name") @NonNull
    private String movieName;

    @ColumnInfo(name = "release_date") @NonNull
    private String releaseDate;

    @ColumnInfo(name = "add_datetime")
    private String addDateTime;

    @ColumnInfo(name = "user_id") @NonNull
    private String userId;


    public Watchlist(String userId, String movieName, String releaseDate, String addDateTime) {
        this.movieName = movieName;
        this.releaseDate = releaseDate;
        this.addDateTime = addDateTime;
        this.userId = userId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getAddDateTime() {
        return addDateTime;
    }

    public void setAddDateTime(String addDateTime) {
        this.addDateTime = addDateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
