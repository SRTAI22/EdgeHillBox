package com.cloudedge.app.Webserver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathManager {

    // initialse the edgehill box folder
    void init_EdgeHillBox() {
        // Locate/Create EdgeHillBox Folder
        File dir = new File("EdgeHillBoxS");

        // check if directory already exist
        if (!dir.exists()) {
            dir.mkdir(); // create directory
            System.out.println("Created Box");
        }
    }

    Path init_User(String username) {
        String dir_name = username + "_box";
        Path path = Paths.get("EdgeHillBoxS", dir_name); // create path to user's box using Paths.get for better path
                                                         // handling

        if (!Files.exists(path)) { // using Files.exists for checking if the directory exists
            try {
                Files.createDirectories(path); // using Files.createDirectories for creating the directory
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return path;
    }

}