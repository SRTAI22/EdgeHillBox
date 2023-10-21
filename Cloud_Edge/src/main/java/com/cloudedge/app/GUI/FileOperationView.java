package com.cloudedge.app.GUI;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileOperationView {

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

    // upload via drag and drop

    // compare client side

}