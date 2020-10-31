package com.laverne.mymoviememoir.Model;

public class MovieBreif {

    private String title;
    private String year;
    private String imageSrc;
    private String imdbID;


    public MovieBreif(String title, String year, String imageSrc, String imdbID) {
        this.title = title;
        this.year = year;
        this.imageSrc = imageSrc;
        this.imdbID = imdbID;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }
}
