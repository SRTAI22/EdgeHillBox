package com.cloudedge.app.GUI;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class ClientAuthView {
    boolean validate_credentials(String username, String password) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/validate");
        JSONObject json = new JSONObject();
        json.put("Username", username);
        json.put("Password", password); // Ideally, this password should be encrypted, not plain text
        CloseableHttpResponse postResponse;
        try {
            StringEntity entity = new StringEntity(json.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            postResponse = httpClient.execute(httpPost);
            if (postResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(postResponse.getEntity());
                System.out.println("POST Response: " + content);
                return true;
            } else {
                System.out.println(
                        "Error with the server. Received code: " + postResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Error while handling HTTP requests. Error: " + e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                System.out.println("Error while closing HttpClient. Error: " + e);
            }
        }
        return false;
    }

    boolean create_user(String username, String password) {
        // code to create a user by send new details to webserver and storing it in
        // credentials file
        return true;
    }
}