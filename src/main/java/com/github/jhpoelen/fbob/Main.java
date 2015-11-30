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
         return (port == null ? "http://localhost:8080" : "http://0.0.0.0/" + port) + "/myapp/";
    }

    public static void main(String[] args) throws IOException {
        System.out.println(String.format("%s starting...", getBaseURI()));
        final HttpServer server = startServer();
        System.out.println(String.format("%s started.", getBaseURI()));

        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(String.format("%s stopping...", getBaseURI()));
                server.shutdownNow();
                System.out.println(String.format("%s stopped.", getBaseURI()));
            }
        }, "shutdownHook"));

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("There was an error while starting Grizzly HTTP server.");
        }
    }
}

