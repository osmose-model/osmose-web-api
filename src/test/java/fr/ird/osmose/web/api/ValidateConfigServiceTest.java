package fr.ird.osmose.web.api;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

public class ValidateConfigServiceTest extends ServerTestBase {

    @Test
    public void validateConfigEmpty() {
        String responseMsg = sendRequest("[]", "validate");
        assertEquals("[]", responseMsg);
    }

    @Test
    public void validateConfig() {
        String responseMsg = sendRequest("[{\"name\":\"donald\"},{\"name\":\"mickey\"}]", "validate");
        assertEquals("[{\"name\":\"donald\"},{\"name\":\"mickey\"}]", responseMsg);
    }

    @Test
    public void validateConfigWithTaxa() throws IOException {
        String groupString = IOUtils.toString(getClass().getResourceAsStream("exampleGroup.json"), "UTF-8");
        String responseMsg = sendRequest(groupString, "validate");
        assertNotNull(responseMsg);
        assertThat(responseMsg, containsString("biotic_resource"));
        assertThat(responseMsg, containsString("focal_functional_group"));
        assertThat(responseMsg, containsString("taxa"));
    }

    @Test
    public void validateConfigWithTaxaImplicit() throws IOException {
        String groupString = IOUtils.toString(getClass().getResourceAsStream("exampleGroupImplicit.json"), "UTF-8");
        String responseMsg = sendRequest(groupString, "validate");
        assertNotNull(responseMsg);
        assertThat(responseMsg, containsString("biotic_resource"));
        assertThat(responseMsg, containsString("implicit"));
        assertThat(responseMsg, containsString("focal_functional_group"));
        assertThat(responseMsg, containsString("taxa"));
    }

    @Test
    public void validateConfigWithTaxaV2() throws IOException {
        String groupString = IOUtils.toString(getClass().getResourceAsStream("exampleConfig.json"), "UTF-8");
        String responseMsg = sendRequest(groupString, "v2/validate");
        assertNotNull(responseMsg);
        assertThat(responseMsg, containsString("biotic"));
        assertThat(responseMsg, containsString("focal_functional_group"));
        assertThat(responseMsg, containsString("taxa"));
    }

    public String sendRequest(String jsonString, String validate) {
        return target
                    .path(validate)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON), String.class);
    }

}
