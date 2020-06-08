package com.google.sps.servlets;

/** Comment class that will hold the comment entity*/
public final class Auth{
    String email;
    String loginUrl;
    String logoutUrl;
    boolean loggedIn; 

    public Auth(String initEmail, String intiLoginUrl, String initLogoutUrl, boolean initLoggedIn){
        email = initEmail;
        loginUrl = intiLoginUrl;
        logoutUrl = initLogoutUrl;
        loggedIn = initLoggedIn;
    }
}