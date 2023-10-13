package com.cloudedge.app.Webserver;

import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.json.JSONObject;
import com.cloudedge.app.Webserver.ClientAuthenticator;

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

            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
            String body = EntityUtils.toString(entity);

            JSONObject json = new JSONObject(body);

            String username = json.getString("Username");
            String password = json.getString("Password");

            // pass extracted values to auth

            ClientAuthenticator clientAuthenticator = new ClientAuthenticator();
            boolean isValid = clientAuthenticator.check_credentials(username, password);

            if (isValid) {
                // json.remove("Username");
                // json.remove("Password");
                json.clear();
                response.setStatusCode(HttpStatus.SC_OK);
                json.put("Valid", true);
                String jsonString = json.toString();

                StringEntity s_entity = new StringEntity(jsonString);
                response.setEntity(s_entity); // String entity
            } else {
                response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            }

        }

        // create user

        // upload files

        // download files

        // sync files

    }

}
