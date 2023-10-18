package com.cloudedge.app.Webserver;

import org.apache.http.HttpResponse;

import org.json.JSONObject;

import com.cloudedge.app.HttpUtils;

public class ServerStatus {
    void handshake(JSONObject json, HttpUtils httpUtils, HttpResponse response) {
        String jsonpayload = "{\"Status\", true }";
        HttpUtils.sendJsonResponse(response, jsonpayload);

    }

}