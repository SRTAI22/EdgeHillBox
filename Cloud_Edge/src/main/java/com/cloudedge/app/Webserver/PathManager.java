package com.cloudedge.app.Webserver;

import java.io.*;

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

    String init_User(String username) {
        String dir_name = username + "_box";
        String path = "\\EdgeHillBoxS\\" + username + "_box\\"; // create path to user's box
        File dir = new File(dir_name);

        if (!dir.exists()) {
            dir.mkdir();
        }

        return path;
    }
}