package com.laverne.mymoviememoir.Entity;

import java.util.Date;

public class User {

    private int userId;
    private String userFname;
    private String userLname;
    private String userGender;
    private String userDob;
    private String userAddress;
    private String userState;
    private String userPostcode;
    private Credentials credId;

    public User(Integer userId, String userFname, String userLname, String userGender, String userDob, String userAddress, String userState, String userPostcode) {
        this.userId = userId;
        this.userFname = userFname;
        this.userLname = userLname;
        this.userGender = userGender;
        this.userDob =userDob;
        this.userAddress =userAddress;
        this.userState = userState;
        this.userPostcode = userPostcode;
    }

    public User(int userId) {
        this.userId = userId;
    }

    public void setCredId(int id) {
        credId = new Credentials(id);
    }

    public int getCredId() {
        return credId.getCredId();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserFname() {
        return userFname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public String getUserLname() {
        return userLname;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserDob() { return userDob; }

    public void setUserDob(String userDob) {
        this.userDob = userDob;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserPostcode() {
        return userPostcode;
    }

    public void setUserPostcode(String userPostcode) {
        this.userPostcode = userPostcode;
    }
}
