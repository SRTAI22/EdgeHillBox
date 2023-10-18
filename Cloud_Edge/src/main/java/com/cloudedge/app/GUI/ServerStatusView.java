package com.cloudedge.app.GUI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.*;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.cloudedge.app.GUI.GUIMain;

public class ServerStatusView {
    // make status call to server
    boolean getStatus() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/status");
        JSONObject json = new JSONObject();
        json.put("Status", false);

        CloseableHttpResponse postResponse;

        try {
            StringEntity entity = new StringEntity(json.toString());
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            postResponse = httpClient.execute(httpPost);
            if (postResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(postResponse.getEntity());
                return true;
            } else {
                System.out.println("Issue connecting to server");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    void connect(JFrame frame) {
        boolean tryconnect = false;
        do {
            tryconnect = getStatus();

            if (tryconnect) {
                GUIMain.login_sign_up_page(frame); // create frame
                System.out.println("Connected"); // debug line
                break;
            } else {
                JOptionPane.showMessageDialog(frame, "Error connecting to server. Re-attempting...", "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while (!tryconnect);
    }

}