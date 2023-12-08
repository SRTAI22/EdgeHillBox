package com.cloudedge.app.Webserver;

import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import com.cloudedge.app.Webserver.RequestHandler.DeletionHandler;
import com.cloudedge.app.Webserver.RequestHandler.ListFiles;
import com.cloudedge.app.Webserver.RequestHandler.CredentialValidation;
import com.cloudedge.app.Webserver.RequestHandler.statusHandler;
import com.cloudedge.app.Webserver.RequestHandler.upload;
import com.cloudedge.app.Webserver.RequestHandler.download;

// local imports
import com.cloudedge.app.Webserver.PathManager;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        // initialise EdgeHillBox folder
        PathManager pathManager = new PathManager();
        // init
        pathManager.init_EdgeHillBox();

        HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8080)
                .registerHandler("/list", new ListFiles())
                .registerHandler("/delete", new DeletionHandler())
                .registerHandler("/validate", new CredentialValidation())
                .registerHandler("/status", new statusHandler())
                .registerHandler("/upload", new upload())
                .registerHandler("/download", new download())
                .create();
        System.out.println("Server on.");

        server.start();
    }
}
