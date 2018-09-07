package fr.ird.osmose.web.api;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertEquals;

public abstract class ServerTestBase {

    private HttpServer server;
    protected WebTarget target;

    @Before
    public void setUp() throws Exception {
        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.getBaseURI());
    }

    @After
    public void tearDown() throws Exception {
        server.shutdownNow();
    }


}
