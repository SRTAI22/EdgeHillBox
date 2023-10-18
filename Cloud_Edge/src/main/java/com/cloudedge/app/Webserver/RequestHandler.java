package com.cloudedge.app.Webserver;

import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.json.JSONObject;
import com.cloudedge.app.Webserver.ClientAuthenticator;
import com.cloudedge.app.HttpUtils;

public class RequestHandler {
    // test handler
    public static class TestHandler implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {

            response.setStatusCode(HttpStatus.SC_OK);
            response.setEntity(new StringEntity("This is a test response"));
        }
    }

    public static class HelloHandler implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase();

            if (method.equals("GET")) {
                // Handle GET request
                try {
                    URI uri = new URI(request.getRequestLine().getUri());
                    String name = uri.getQuery().split("=")[1];
                    response.setStatusCode(HttpStatus.SC_OK);
                    response.setEntity(new StringEntity("Hello, " + name + " the test worked!!"));
                } catch (URISyntaxException | ArrayIndexOutOfBoundsException e) {
                    response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                }
            } else if (method.equals("POST")) {
                // Handle POST request
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                String body = EntityUtils.toString(entity);

                try {
                    // Parse JSON
                    JSONObject json = new JSONObject(body);

                    // Extract the 'name' field from JSON
                    String name = json.getString("Name");

                    // Create response
                    response.setStatusCode(HttpStatus.SC_OK);
                    response.setEntity(new StringEntity("Hello, " + name + " the test worked!!"));
                } catch (Exception e) { // JSON Parsing error
                    response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                    response.setEntity(new StringEntity("Invalid JSON format"));
                }
            } else {
                // Unsupported method
                response.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    // password validation

    public static class CredentialValidation implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {
            String method = request.getRequestLine().getMethod().toUpperCase();

            if (method.equals("POST")) {
                String header = request.getFirstHeader("Action").getValue();
                if (header.equals("ValidateUser")) {
                    ValidateUser(request, response, context);
                } else if (header.equals("AddUser")) {
                    AddUser(request, response, context);
                } else {
                    response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                }
            } else {
                response.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
            }
        }

        public void ValidateUser(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {

            // entity
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

            // Create HttpIUtils parse validation objects
            HttpUtils httpUtils = new HttpUtils();

            // parse json payload
            JSONObject json = HttpUtils.parseValidationRequestBody(entity, request);
            String username = json.getString("Username");
            String password = json.getString("Password");

            // pass extracted values to auth

            ClientAuthenticator clientAuthenticator = new ClientAuthenticator();
            boolean isValid = clientAuthenticator.check_credentials(username, password);

            if (isValid) {
                json.clear(); // remove extrated username ans password from json object
                String jsonPayload = "{\"Valid\":true}";
                HttpUtils.sendJsonResponse(response, jsonPayload);
            } else {
                response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            }

        }

        // create user
        public void AddUser(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {

            // entity
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

            // Create HttpIUtils parse validation objects
            HttpUtils httpUtils = new HttpUtils();

            // parse json payload
            JSONObject json = HttpUtils.parseValidationRequestBody(entity, request);
            String username = json.getString("Username");
            String password = json.getString("Password");

            // pass extracted values to auth

            ClientAuthenticator clientAuthenticator = new ClientAuthenticator();
            boolean isValid = clientAuthenticator.append_new_user(username, password);

            if (isValid) {
                json.clear(); // remove extrated username ans password from json object
                String jsonPayload = "{\"Created\":true}";
                HttpUtils.sendJsonResponse(response, jsonPayload);
            } else {
                response.setStatusCode(HttpStatus.SC_CONFLICT);
            }

        }
    }

    // Status code

    public static class statusHandler implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {

            // entity
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

            // Create HttpIUtils parse validation objects
            HttpUtils httpUtils = new HttpUtils();

            JSONObject json = HttpUtils.parseValidationRequestBody(entity, request);

            ServerStatus serverStatus = new ServerStatus();

            serverStatus.handshake(json, httpUtils, response); // return status

        }
    }

    // upload files

    // download filesc

    // sync files

}