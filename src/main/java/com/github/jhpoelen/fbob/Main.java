package com.github.jhpoelen.fbob;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("com.github.jhpoelen.fbob");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(getBaseURI()), rc);
    }

    public static String getBaseURI() {
        String port = System.getenv("PORT");
        return (port == null ? "http://localhost:8080" : ("http://0.0.0.0:" + port)) + "/";
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.shutdownNow();
            }
        }, "shutdownHook"));

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("There was an error while starting Grizzly HTTP server.");
        }
    }
}

