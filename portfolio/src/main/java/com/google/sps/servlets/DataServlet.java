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
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import com.google.sps.servlets.Comment;

/** Servlet that returns some example content.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {  
        long reqNum = Long.parseLong(request.getParameter("num"));
        String reqLang = request.getParameter("lang");
        Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

        PreparedQuery results = DatastoreServiceFactory.getDatastoreService().prepare(query);
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        List<Comment> comments = new ArrayList<>();

        int i = 0;
        for (Entity entity : results.asIterable()) {
            if(i == reqNum){
                break;
            }
            long id = entity.getKey().getId();
            String commentText = (String) entity.getProperty("commentText");
            String email = (String) entity.getProperty("email");
            long timestamp = (long) entity.getProperty("timestamp");

            Translation translation = translate.translate(commentText, Translate.TranslateOption.targetLanguage(reqLang));
            String translatedText = translation.getTranslatedText();

            Comment commentInstance = new Comment(translatedText, email, timestamp, id);
            comments.add(commentInstance);

            i = i + 1;
        }

        Gson gson = new Gson();
        String json = gson.toJson(comments);
 
        // Send the JSON as the response.
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String jsonString = IOUtils.toString(request.getInputStream());
        Comment commentReq = new Gson().fromJson(jsonString, Comment.class);

        String commentText = commentReq.comment;
        String email = commentReq.email;
        long timestamp = System.currentTimeMillis();
 
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("commentText", commentText);
        commentEntity.setProperty("email", email);
        commentEntity.setProperty("timestamp", timestamp);
 
        DatastoreServiceFactory.getDatastoreService().put(commentEntity);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long id = Long.parseLong(request.getParameter("id"));
        Key commentEntityKey = KeyFactory.createKey("Comment", id);
        DatastoreServiceFactory.getDatastoreService().delete(commentEntityKey);
    }
}

