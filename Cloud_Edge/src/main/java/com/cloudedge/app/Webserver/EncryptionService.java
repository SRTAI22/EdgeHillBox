package com.cloudedge.app.Webserver;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EncryptionService {
    void serialize(String file, Map<String, String> creds) {
        try {
            FileOutputStream fileout = new FileOutputStream("Credentials.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileout);

            out.writeObject(creds);
            out.close();
            fileout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Map<String, String> deserialize(String file) throws IOException, ClassNotFoundException {
        if (!Files.exists(Paths.get(file))) {
            return new HashMap<>();
        }
        FileInputStream fileIn = null;
        ObjectInputStream in = null;
        try {
            fileIn = new FileInputStream(file);
            in = new ObjectInputStream(fileIn);
            Map<String, String> recoveredMap = (Map<String, String>) in.readObject();
            return recoveredMap;
        } finally {
            closeQuietly(in);
            closeQuietly(fileIn);
        }
    }

    private void closeQuietly(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                // log error here
                e.printStackTrace();
            }
        }
    }

}