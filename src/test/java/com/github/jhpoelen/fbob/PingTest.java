package com.github.jhpoelen.fbob;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PingTest extends ServerTestBase {

    @Test
    public void ping() {
        String responseMsg = target.path("ping").request().get(String.class);
        assertEquals("pong", responseMsg);
    }

    @Test
    public void postPing() {
        String jsonString = "{ \"msg\": \"ping\", \"type\":\"request\" }";
        String responseMsg = target
                .path("ping")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON), String.class);
        assertEquals("{\"msg\":\"pong\",\"type\":\"response\"}", responseMsg);
    }


}
