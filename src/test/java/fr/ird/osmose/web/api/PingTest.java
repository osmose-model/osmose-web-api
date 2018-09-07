package fr.ird.osmose.web.api;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

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
