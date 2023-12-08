package com.cloudedge.app.Client;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.cloudedge.app.GUI.GUIMain;

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
    public boolean validate_credentials(JFrame frame, String username, String password) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/validate");
        JSONObject json = new JSONObject();
        json.put("Username", username);
        json.put("Password", password);
        CloseableHttpResponse postResponse;
        try {
            StringEntity entity = new StringEntity(json.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Action", "ValidateUser");
            postResponse = httpClient.execute(httpPost);
            if (postResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(postResponse.getEntity());
                System.out.println("POST Response: " + content);
                return true;
            } else {
                System.out.println(
                        "Error with the server. Received code: " + postResponse.getStatusLine().getStatusCode());
                if (postResponse.getStatusLine().getStatusCode() == 404) {
                    // display incorect details entered message
                    JOptionPane.showMessageDialog(frame, "Incorrect details, try again.", "Details Error", 0);
                } else {
                    JOptionPane.showMessageDialog(frame, "Error: " + postResponse.getStatusLine().getStatusCode(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            System.out.println("Error while handling HTTP requests. Error: " + e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                System.out.println("Error while closing HttpClient. Error: " + e); // add check to see error code and
                                                                                   // display error log
            }
        }
        return false;
    }

    public boolean create_user(JFrame frame, String username, String password) {
        // code to create a user by sending new details to webserver and storing it in
        // credentials file
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/validate");
        JSONObject json = new JSONObject();
        json.put("Username", username);
        json.put("Password", password);
        CloseableHttpResponse postResponse;
        try {
            StringEntity entity = new StringEntity(json.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Action", "AddUser");
            postResponse = httpClient.execute(httpPost);
            if (postResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(postResponse.getEntity());
                System.out.println("POST Response: " + content);
                return true;
            } else {
                System.out.println(
                        "Error with the server. Received code: " + postResponse.getStatusLine().getStatusCode());

                if (postResponse.getStatusLine().getStatusCode() == 409) {
                    String[] options = new String[] { "Log In", "Okay" };
                    int response = JOptionPane.showOptionDialog(frame,
                            "User already exists, please enter a new username or log in.", "Username Taken",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (response == 0) {
                        // Send to login page
                        GUIMain.login_page(frame);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Error: " + postResponse.getStatusLine().getStatusCode(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        } catch (Exception e) {
            System.out.println("Error while handling HTTP requests. Error: " + e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                System.out.println("Error while closing HttpClient. Error: " + e); // add check to see error code and
                                                                                   // display error log
            }
        }
        return false;
    }
}