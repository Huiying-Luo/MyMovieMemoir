package com.laverne.mymoviememoir.Entity;

import java.util.Date;

public class Credentials {

    private int credId;
    private String credUsername;
    private String credPasswordhash;
    private String credSignupDate;

    public Credentials() {}

    public Credentials(int credId, String credUsername, String credPasswordhash, String credSignupDate) {
        this.credId = credId;
        this.credUsername = credUsername;
        this.credPasswordhash = credPasswordhash;
        this.credSignupDate = credSignupDate;
    }

    public  Credentials(int credId) {
        this.credId = credId;
    }

    public int getCredId() {
        return credId;
    }

    public void setCredId(int credId) {
        this.credId = credId;
    }

    public String getCredUsername() {
        return credUsername;
    }

    public void setCredUsername(String credUsername) {
        this.credUsername = credUsername;
    }

    public String getCredPasswordhash() {
        return credPasswordhash;
    }

    public void setCredPasswordhash(String credPasswordhash) { this.credPasswordhash = credPasswordhash; }

    public String getCredSignupDate() {
        return credSignupDate;
    }

    public void setCredSignupDate(String credSignupDate) {
        this.credSignupDate = credSignupDate;
    }
}
