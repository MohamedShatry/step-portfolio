package com.google.sps.servlets;

/** Comment class that will hold the comment entity*/
public final class Comment{
    String comment;
    String email;
    long timestamp;
    long id;

    public Comment(String initComment, String initEmail, long initTimestamp, long initId){
        comment = initComment;
        email = initEmail;
        timestamp = initTimestamp;
        id = initId;
    }
}