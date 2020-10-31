package com.laverne.mymoviememoir.Entity;

public class Memoir {
    //server-side fields
    private int memId;
    private String movieName;
    private String movieReleaseDate;
    private String memDatetime;
    private double memRating;
    private String memComment;
    private Cinema cineId;
    private User userId;

    // client side
    private String imageSrc;
    private String genre;
    private double publicRating;




    public Memoir(int memId, String movieName, String movieReleasedate, String memDatetime, double memRating, String memComment) {
        this.memId = memId;
        this.movieName = movieName;
        this.movieReleaseDate = movieReleasedate;
        this.memDatetime = memDatetime;
        this.memRating = memRating;
        this.memComment = memComment;
    }

    public Memoir(String movieName, String movieReleasedate, String memDatetime, double memRating, String memComment) {
        this.movieName = movieName;
        this.movieReleaseDate = movieReleasedate;
        this.memDatetime = memDatetime;
        this.memComment = memComment;
        this.memRating = memRating;
    }

    public void setCineId(String name, String postcode) {
        cineId = new Cinema(name, postcode);
    }

    public void setCineId(int id) {
        cineId = new Cinema(id);
    }

    public void setUserId(Integer id) {
        userId = new User(id);
    }

    public int getUserId() {
        return userId.getUserId();
    }

    public Cinema getCineId() {
        return cineId;
    }

    public int getMemId() {
        return memId;
    }

    public void setMemId(int memId) {
        this.memId = memId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieReleasedate() {
        return movieReleaseDate;
    }

    public void setMovieReleasedate(String movieReleasedate) {
        this.movieReleaseDate = movieReleasedate;
    }

    public String getMemDatetime() {
        return memDatetime;
    }

    public void setMemDatetime(String memDatetime) {
        this.memDatetime = memDatetime;
    }

    public double getMemRating() {
        return memRating;
    }

    public void setMemRating(double memRating) {
        this.memRating = memRating;
    }

    public String getMemComment() {
        return memComment;
    }

    public void setMemComment(String memComment) {
        this.memComment = memComment;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getPublicRating() {
        return publicRating;
    }

    public void setPublicRating(double publicRating) {
        this.publicRating = publicRating;
    }
}
