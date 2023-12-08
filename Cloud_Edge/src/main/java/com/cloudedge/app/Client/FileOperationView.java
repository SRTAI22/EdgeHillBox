package com.cloudedge.app.Client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileOperationView {

    // create edgehillbox folder
    public String createCloudEdgeBoxFolder() {
        // Locate/Create EdgeHillBox Folder
        File dir = new File("CloudEdgeBox");

        // check if directory already exists
        if (!dir.exists()) {
            if (dir.mkdir()) { // create directory
                return dir.getAbsolutePath();
            }
        }
        return dir.getAbsolutePath();
    }

    // check if local Box exists
    public boolean Check_local_Box() {
        // Locate EdgeHillBox Folder
        File dir = new File("CloudEdgeBox");

        // check if directory exists
        if (dir.exists()) {
            return true;
        } else {
            createCloudEdgeBoxFolder();
        }
        return false;
    }

    // get files from local Box
    public List<Path> getFilesFromLocalBox() {
        List<Path> files = new ArrayList<>();
        File dir = new File("CloudEdgeBox");

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
    public String calculateChecksum(Path file) {
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

    // send local files for comparison
    public List<String> fileSyncCheck(Map<String, String> filesum) {
        try {
            // Create HttpClient
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // Create HttpPost
            HttpPost post = new HttpPost("http://localhost:8080/upload");
            post.addHeader("Sync-Check", null);

            // Convert the map to JSON
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(filesum);

            // DEBUG: Print to check JSON string
            System.out.println("DEBUG: Sending JSON: " + json);

            // Set the request body
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(entity);

            // Execute and get the response
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity responseEntity = response.getEntity();
                String result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);

                if (response.getStatusLine().getStatusCode() == 409) {
                    System.out.println("Files not in sync");

                    String[] files = result.split(": ")[1].split(", ");

                    List<String> paths = new ArrayList<>();

                    for (String file : files) {
                        String path = getFileByName(file).toString();

                        paths.add(path);
                    }

                    return paths;
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
    public Boolean uploadfiles(List<Path> files) {
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
        int counter = 1; // Initialise a counter variable
        for (Path filePath : files) {
            System.out.println("Currently processing file: " + filePath);
            File file = filePath.toFile();
            builder.addBinaryBody("file" + counter, file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            counter++; // Increment the counter for each file
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
    // compare client side

    // convert string path to path
    private static Path convertStringToPath(String desyncFile) {
        return Paths.get(desyncFile.trim());
    }

    private final Path edgeHillBoxDirectory = convertStringToPath(createCloudEdgeBoxFolder());

    public Boolean downloadFiles(List<String> selectedFilesToDownload) throws IOException {
        System.out.println("Downloading files...");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost downloadFile = new HttpPost("http://localhost:8080/download");
            downloadFile.addHeader("file-download", "true");

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(selectedFilesToDownload);
            StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            downloadFile.setEntity(requestEntity);

            try (CloseableHttpResponse response = httpClient.execute(downloadFile)) {
                HttpEntity responseEntity = response.getEntity();

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && responseEntity != null) {
                    // Get the boundary from the Content-Type header
                    String boundary = extractBoundary(responseEntity.getContentType().getValue());

                    if (boundary != null) {
                        // Create a MultipartStream to parse the response
                        MultipartStream multipartStream = new MultipartStream(responseEntity.getContent(),
                                boundary.getBytes(), 4096, null);

                        boolean nextPart = multipartStream.skipPreamble();
                        while (nextPart) {
                            String headers = multipartStream.readHeaders();
                            String fileName = extractFileNameFromHeaders(headers);

                            if (fileName != null) {
                                Path fileToWritePath = edgeHillBoxDirectory.resolve(fileName);
                                try (BufferedOutputStream out = new BufferedOutputStream(
                                        new FileOutputStream(fileToWritePath.toFile(), false))) {
                                    multipartStream.readBodyData(out);
                                }
                                System.out.println("Downloaded and saved: " + fileName);
                            }

                            nextPart = multipartStream.readBoundary();
                        }
                    }
                } else {
                    // Handle response codes other than 200 OK here
                    System.out.println(
                            "Failed to download files. Status code: " + response.getStatusLine().getStatusCode());
                    return false;
                }
                System.out.println("All files downloaded successfully.");
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error while downloading files. Error: " + e);
            return false;
        }
    }

    private String extractBoundary(String contentType) {
        Pattern pattern = Pattern.compile("boundary=(.*)");
        Matcher matcher = pattern.matcher(contentType);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractFileNameFromHeaders(String headers) {
        Pattern pattern = Pattern.compile("filename=\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(headers);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String getFileName(Header contentDispositionHeader) {
        // Parse the Content-Disposition header to extract the filename
        HeaderElement[] elements = contentDispositionHeader.getElements();
        for (HeaderElement element : elements) {
            NameValuePair nameValuePair = element.getParameterByName("filename");
            if (nameValuePair != null) {
                return nameValuePair.getValue();
            }
        }
        return null;
    }

    // compare client side

    // List remote files
    public List<String> getRemoteFiles() throws IOException {
        List<String> remoteList = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost listfile = new HttpPost("http://localhost:8080/list");

            // set valid header
            listfile.setHeader("list-remote", "true");

            try (CloseableHttpResponse response = httpClient.execute(listfile)) {
                HttpEntity resHttpEntity = response.getEntity();
                String remoteFiles = EntityUtils.toString(resHttpEntity);
                remoteList = Arrays.asList(remoteFiles.split("\\s*,\\s*"));
            } catch (ClientProtocolException cpe) {
                System.out.println("Client Protocol Exception while getting list of remote files. Error: " + cpe);
                throw cpe;
            } catch (IOException ioe) {
                System.out.println("IO Exception while getting list of remote files. Error: " + ioe);
                throw ioe;
            }
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid URL or header while getting list of remote files. Error: " + iae);
            throw iae;
        }
        return remoteList;

    }

    // delete files
    public Boolean deleteFiles(List<String> selectedFilesToDelete) throws IOException {
        System.out.println("Deleting files...");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost deleteFile = new HttpPost("http://localhost:8080/delete");

            // set valid header
            deleteFile.setHeader("delete-files", "true");

            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(selectedFilesToDelete);
            StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            deleteFile.setEntity(requestEntity);

            try (CloseableHttpResponse response = httpClient.execute(deleteFile)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity responseEntity = response.getEntity();
                    String result = EntityUtils.toString(responseEntity);
                    System.out.println("Server Response: " + result);
                    return true;
                } else {
                    System.out.println("Failed to delete files. Status code: " + statusCode);
                    return false;
                }
            } catch (ClientProtocolException cpe) {
                System.out.println("Client Protocol Exception while deleting files. Error: " + cpe);
                throw cpe;
            } catch (IOException ioe) {
                System.out.println("IO Exception while deleting files. Error: " + ioe);
                throw ioe;
            }
        }
    }

}
