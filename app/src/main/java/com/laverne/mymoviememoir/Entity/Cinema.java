package com.laverne.mymoviememoir.Entity;

public class Cinema {

    private Integer cineId;
    private String cineName;
    private String cinePostcode;

    public Cinema(Integer cineId, String cineName, String cinePostcode) {
        this.cineId = cineId;
        this.cineName = cineName;
        this.cinePostcode = cinePostcode;
    }


    public Cinema(String cineName, String cinePostcode) {
        this.cineName = cineName;
        this.cinePostcode = cinePostcode;
    }


    public Cinema(int cineId) {
        this.cineId = cineId;
    }

    public Integer getCineId() {
        return cineId;
    }

    public void setCineId(Integer cineId) {
        this.cineId = cineId;
    }

    public String getCineName() {
        return cineName;
    }

    public void setCineName(String cineName) {
        this.cineName = cineName;
    }

    public String getCinePostcode() {
        return cinePostcode;
    }

    public void setCinePostcode(String cinePostcode) {
        this.cinePostcode = cinePostcode;
    }
}
