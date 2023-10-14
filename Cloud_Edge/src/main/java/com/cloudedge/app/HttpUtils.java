package com.cloudedge.app;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.json.JSONObject;

public class HttpUtils {

    // Response methods
    public static HttpResponse createOkResponse() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        // Additional common headers can be set here
        return response;
    }

    public static HttpResponse createNotFoundResponse() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_NOT_FOUND, "Not Found");
        // Additional common headers can be set here
        return response;
    }

    // pre-configurations
    public static HttpRequest createGetRequest(String uri) {
        HttpGet httpGet = new HttpGet(uri);
        // Set common headers or configurations
        return httpGet;
    }

    public static HttpRequest createPostRequest(String uri) {
        HttpPost httpPost = new HttpPost(uri);
        // Set common headers or configurations
        return httpPost;
    }

    // hanlde incoming payload
    public static JSONObject parseValidationRequestBody(HttpEntity entity, HttpRequest request)
            throws ParseException, IOException {
        // parse json payload
        String body = EntityUtils.toString(entity);

        JSONObject json = new JSONObject(body);

        return json;
    }

    // handle outgoing payload
    public static void sendJsonResponse(HttpResponse response, String jsonPayload) {
        try {
            StringEntity entity = new StringEntity(jsonPayload);
            response.setHeader("Content-Type", "application/json");
            response.setEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addCorsHeaders(HttpResponse response) {
        // Add CORS headers to the HttpResponse
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token");
    }

}