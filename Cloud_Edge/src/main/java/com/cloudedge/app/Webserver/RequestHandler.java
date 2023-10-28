package com.cloudedge.app.Webserver;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;
import com.cloudedge.app.Webserver.ClientAuthenticator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cloudedge.app.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//file upload imports
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestHandler {
    private static String username;

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
            username = json.getString("Username");
            String password = json.getString("Password");

            // pass extracted values to auth

            ClientAuthenticator clientAuthenticator = new ClientAuthenticator();
            boolean isValid = clientAuthenticator.checkCredentials(username, password);

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
            username = json.getString("Username");
            String password = json.getString("Password");

            // pass extracted values to auth

            ClientAuthenticator clientAuthenticator = new ClientAuthenticator();
            boolean isValid = clientAuthenticator.appendNewUser(username, password);

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
    public static class upload implements HttpRequestHandler {
        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {
            // Check headers for sync-check
            if (request.containsHeader("sync-check")) {
                // Call sync check function
                syncCheck(request, response, context);
            }
            // Check headers for file-upload
            else if (request.containsHeader("file-upload")) {
                // Call file upload function
                fileUpload(request, response, context);
            }
            // Invalid request
            else {
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                StringEntity entity = new StringEntity("Invalid request");
                response.setEntity(entity);
            }
        }

        public void fileUpload(HttpRequest request, HttpResponse response, HttpContext context) throws IOException {
            // Check headers for file-upload
            if (request.containsHeader("file-upload")) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

                long len = entity.getContentLength();
                System.out.println("Content Length: " + len);

                if (entity != null) {
                    // Parse the incoming HttpEntity to extract files.
                    List<File> uploadedFiles = parseIncomingFiles(entity);
                    System.out.println("Files received: " + uploadedFiles);

                    // Assume that FileManager and PathManager are working correctly
                    FileManager fileManager = new FileManager();
                    PathManager pathManager = new PathManager();
                    Path path = pathManager.init_User(username);

                    // Add the files to the user's box
                    for (File file : uploadedFiles) {
                        System.out.println("Processing: " + file);
                        Path filePath = path.resolve(file.getName());
                        Files.copy(file.toPath(), filePath);
                    }

                    // Set the response status code and message
                    response.setStatusCode(HttpStatus.SC_OK);
                    StringEntity stringEntity = new StringEntity("Files uploaded successfully");
                    response.setEntity(stringEntity);
                }
            } else {
                // Invalid request
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                StringEntity stringEntity = new StringEntity("Invalid request");
                response.setEntity(stringEntity);
            }
        }

        private List<File> parseIncomingFiles(HttpEntity entity) throws IOException {
            List<File> uploadedFiles = new ArrayList<>();

            System.out.println("Parsing incoming files...");

            // Extract boundary from content type
            Header contentTypeHeader = entity.getContentType();
            String boundary = null;
            if (contentTypeHeader != null) {
                String[] parts = contentTypeHeader.getValue().split(";");
                for (String part : parts) {
                    if (part.trim().startsWith("boundary=")) {
                        boundary = part.trim().substring("boundary=".length());
                        break;
                    }
                }
            }

            if (boundary == null) {
                System.out.println("Boundary not found in content type");
                return uploadedFiles;
            }

            MultipartStream multipartStream = new MultipartStream(entity.getContent(),
                    boundary.getBytes(), 4096, null);
            boolean nextPart = multipartStream.skipPreamble();
            while (nextPart) {
                String header = multipartStream.readHeaders();
                System.out.println("Header: " + header);

                File tempFile = File.createTempFile("upload", ".tmp");
                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    multipartStream.readBodyData(outputStream);
                }
                uploadedFiles.add(tempFile);

                nextPart = multipartStream.readBoundary();
            }

            System.out.println("Files received: " + uploadedFiles);

            return uploadedFiles;
        }

        private String extractFileName(String header) {
            Pattern fileNamePattern = Pattern.compile("filename=\"([^\"]+)\"");
            Matcher matcher = fileNamePattern.matcher(header);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return null; // or generate a random/temporary name, depending on your requirement
            }
        }

        // Sync check function
        private void syncCheck(HttpRequest request, HttpResponse response, HttpContext context)
                throws HttpException, IOException {
            // init user
            PathManager pathManager = new PathManager();
            FileManager fileManager = new FileManager();
            Path path = pathManager.init_User(username);
            List<Path> files = fileManager.getFilesFromLocalBox(path);

            // Get the sent hash map from the HTTP POST
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
            String requestBody = EntityUtils.toString(entity);
            Map<String, String> sentHashMap = new Gson().fromJson(requestBody,
                    new TypeToken<HashMap<String, String>>() {
                    }.getType());

            Map<String, String> localFileToChecksumMap = new HashMap<>();
            for (Path file : files) {
                String checksum = fileManager.calculateChecksum(file);
                localFileToChecksumMap.put(file.toString(), checksum);
            }

            // Check if local and remote checksums match for each file
            List<String> filesNeedSync = new ArrayList<>();

            for (String remoteFile : sentHashMap.keySet()) {
                if (!remoteFile.equals(sentHashMap.get(remoteFile))) {
                    filesNeedSync.add(remoteFile);
                }
            }

            if (!filesNeedSync.isEmpty()) {
                response.setStatusCode(HttpStatus.SC_CONFLICT);
                StringEntity entityRes = new StringEntity(
                        "The following files need to be synced: " + String.join(", ", filesNeedSync));
                response.setEntity(entityRes);
                return;
            }

            response.setStatusCode(HttpStatus.SC_OK);
            StringEntity entityRes = new StringEntity("Files are already in sync.");
            response.setEntity(entityRes);
        }

        // download filesc

    }
}