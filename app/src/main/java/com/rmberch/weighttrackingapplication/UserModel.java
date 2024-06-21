package com.rmberch.weighttrackingapplication;


//Class for User Model. Has Id, username, and password
//Setters and getters
public class UserModel {
  //Class variables
    private int id;
    private String username;
    private String password;

    //Constructors
    public UserModel(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public UserModel() {
    }

    //Setters and Getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
