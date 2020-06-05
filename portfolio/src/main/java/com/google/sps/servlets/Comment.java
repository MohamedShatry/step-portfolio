package com.google.sps.servlets;

/** Comment class that will hold the comment entity*/
public final class Comment{
    String comment;
    String userName;
    long timestamp;
    long id;

    public Comment(String initComment, String initUserName, long initTimestamp, long initId){
        comment = initComment;
        userName = initUserName;
        timestamp = initTimestamp;
        id = initId;
    }
}