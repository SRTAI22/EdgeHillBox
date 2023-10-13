package com.cloudedge.app;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.HttpResponse;
import java.util.Map;
import org.apache.http.HttpEntity;
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
    public static String parseRequestBody(HttpEntity entity) {
        return null;
        // Parse the request body and return as string
    }

    public static Map<String, String> parseQueryParams(String uri) {
        return null;
        // Parse query parameters from URI and return as a map
    }

    // handle outgoing payload
    public static void sendJsonResponse(HttpResponse response, String jsonPayload) { // potential type arugment which
                                                                                     // will help function knwo what
                                                                                     // type of format to follow
        // Set JSON payload to HttpResponse and set appropriate headers
        JSONObject json = new JSONObject();

    }

    public static void addCorsHeaders(HttpResponse response) {
        // Add CORS headers to the HttpResponse
    }

}