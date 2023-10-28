package com.cloudedge.app.GUI;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileOperationView {

    // create edgehillbox folder
    boolean Check_local_Box() {
        // Locate/Create EdgeHillBox Folder
        File dir = new File("EdgeHillBox");

        // check if directory already exist
        if (!dir.exists()) {
            dir.mkdir(); // create directory
            return true;
        } else if (dir.exists()) {
            return true;
        } else {
            return false;
        }
    }

    // get files from local Box
    public List<Path> getFilesFromLocalBox() {
        List<Path> files = new ArrayList<>();
        File dir = new File("EdgeHillBox");

        if (dir.exists() && dir.isDirectory()) {
            File[] filesInDir = dir.listFiles();
            for (File file : filesInDir) {
                if (file.isFile()) {
                    files.add(file.toPath());
                }
            }
        }
        return files;
    }

    // Calculate checksum
    String calculateChecksum(Path file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            try (FileInputStream fileInput = new FileInputStream(file.toFile())) {
                byte[] dataBytes = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInput.read(dataBytes)) != -1) {
                    messageDigest.update(dataBytes, 0, bytesRead);
                }
            }

            byte[] digestBytes = messageDigest.digest();
            BigInteger bigInt = new BigInteger(1, digestBytes);

            // Converting to hexadecimal might lead to leading zeros being removed.
            String hash = bigInt.toString(16);

            // As SHA-256 generates a fixed size hash, we pad it with zeros at the beginning
            // if required.
            while (hash.length() < 64) {
                hash = "0" + hash;
            }
            return hash;

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // upload via drag and drop

    // send local files for comparison
    String fileSyncCheck(Map<String, String> filesum) {
        try {
            // Create HttpClient
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // Create HttpPost
            HttpPost post = new HttpPost("http://localhost:8080/upload");
            post.addHeader("Sync-Check", null);
            // Convert the map to JSON
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(filesum);
            // Set the request body
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            // Execute and get the response
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity responseEntity = response.getEntity();
                String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("Response: " + result);
                if (response.getStatusLine().getStatusCode() == 409) {
                    System.out.println("Files not in sync");
                    String[] files = result.split(": ")[1].split(", ");
                    for (String file : files) {
                        String path = getFileByName(file).toString();
                        return path;
                    }
                }

                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Path getFileByName(String fileName) {
        List<Path> files = getFilesFromLocalBox();
        for (Path file : files) {
            if (file.getFileName().toString().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    // upload file
    Boolean uploadfiles(List<Path> files) {
        System.out.println("Uploading...");

        // Create HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create HttpPost
        HttpPost uploadFile = new HttpPost("http://localhost:8080/upload");

        // Add custom header to indicate this is a file upload request
        uploadFile.addHeader("file-upload", "true");

        // Create multipart entity and add the form fields
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        System.out.println("Files to be uploaded: " + files);

        // Loop over each file path and add it to the multipart entity
        for (Path filePath : files) {
            System.out.println("Currently processing file: " + filePath);
            File file = filePath.toFile();
            builder.addBinaryBody("files", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        }

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        // Execute and get the response
        try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
            HttpEntity responseEntity = response.getEntity();
            String result = EntityUtils.toString(responseEntity);
            System.out.println("Server Response: " + result);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Download files

    // compare client side

}