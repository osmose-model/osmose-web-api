package com.github.jhpoelen.fbob;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

public class ValidateConfigTest extends ServerTestBase {

    @Test
    public void validateConfigEmpty() {
        String responseMsg = sendRequest("[]");
        assertEquals("[]", responseMsg);
    }

    @Test
    public void validateConfig() {
        String responseMsg = sendRequest("[{\"name\":\"donald\"},{\"name\":\"mickey\"}]");
        assertEquals("[{\"name\":\"donald\"},{\"name\":\"mickey\"}]", responseMsg);
    }

    @Test
    public void validateConfigWithTaxa() throws IOException {
        String groupString = IOUtils.toString(getClass().getResourceAsStream("exampleGroup.json"), "UTF-8");
        String responseMsg = sendRequest(groupString);
        assertNotNull(responseMsg);
        assertThat(responseMsg, containsString("background"));
        assertThat(responseMsg, containsString("focal"));
        assertThat(responseMsg, containsString("taxa"));
    }

    public String sendRequest(String jsonString) {
        return target
                    .path("validate")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON), String.class);
    }

}