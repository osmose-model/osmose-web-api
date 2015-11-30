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
         return "http://localhost:" + (port == null ? 8080 : port) + "/myapp/";
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit CTRL^C to stop it...", getBaseURI()));

        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Stopping server..");
                server.shutdownNow();
            }
        }, "shutdownHook"));

        try {
            server.start();
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("There was an error while starting Grizzly HTTP server.");
        }
    }
}

