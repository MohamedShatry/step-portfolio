// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import com.google.sps.servlets.Auth;

/** Servlet that returns some example content.*/
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {  
        String email = "";
        String loginUrl = "";
        String logoutUrl = "";
        boolean loggedIn = false; 
        
        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn()) {
            email = userService.getCurrentUser().getEmail();
            logoutUrl = userService.createLogoutURL("/comments.html");
            loggedIn = true;
        } else {
            loginUrl = userService.createLoginURL("/comments.html");
            loggedIn = false;
        }
        
        Auth credentials = new Auth(email, loginUrl, logoutUrl, loggedIn);
        
        Gson gson = new Gson();
        String json = gson.toJson(credentials);
 
        // Send the JSON as the response.
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

}

