package com.cloudedge.app.Webserver;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    // add files to folder
    void addFiles(Path userpath, List<File> filesToAdd) {
        File dir = new File(userpath.toString());
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Invalid directory path");
            return;
        }
        for (File file : filesToAdd) {
            Path destPath = Paths.get(userpath.toString(), file.getName());
            try {
                Files.copy(file.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // get files
    public List<Path> getFilesFromLocalBox(Path path) {
        List<Path> files = new ArrayList<>();
        File dir = path.toFile();

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

    // compare serverside

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

}