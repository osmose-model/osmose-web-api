package fr.ird.osmose.web.api;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static HttpServer startServer() {
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(getBaseURI()), createApp());
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

    public static ResourceConfig createApp() {
        return new ResourceConfig()
                .packages("com.github.jhpoelen.fbob")
                .register(CORSResponseFilter.class)
                .register(createMoxyJsonResolver());
    }

    public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
        return moxyJsonConfig.resolver();
    }
}

