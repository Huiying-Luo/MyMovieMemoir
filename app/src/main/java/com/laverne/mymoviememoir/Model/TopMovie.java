package com.laverne.mymoviememoir.Model;

public class TopMovie {

    private String name;
    private String date;
    private Double score;

    public TopMovie(String name, String date, Double score) {
        this.name = name;
        this.date = date;
        this.score = score;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
