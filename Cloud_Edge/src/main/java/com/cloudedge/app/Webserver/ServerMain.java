package com.cloudedge.app.Webserver;

import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import com.cloudedge.app.Webserver.RequestHandler.HelloHandler;
import com.cloudedge.app.Webserver.RequestHandler.TestHandler;
import com.cloudedge.app.Webserver.RequestHandler.CredentialValidation;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8080)
                .registerHandler("/test", new TestHandler())
                .registerHandler("/hello", new HelloHandler())
                .registerHandler("/validate", new CredentialValidation())
                .create();
        System.out.println("Server on.");

        server.start();
    }
}
