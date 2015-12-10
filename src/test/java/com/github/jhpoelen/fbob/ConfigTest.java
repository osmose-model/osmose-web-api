package com.github.jhpoelen.fbob;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class ConfigTest {

    @Test
    public void archive() throws IOException {
        Response actual = new Config().configArchive();
        assertThat(actual, is(notNullValue()));
        assertThat(actual.hasEntity(), is(true));
        StreamingOutput os = (StreamingOutput)actual.getEntity();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        os.write(outputStream);
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        assertThat(zis, is(notNullValue()));
        ZipEntry entry;
        while((entry = zis.getNextEntry()) != null) {
            assertThat(entry.getName(), is(notNullValue()));
            IOUtils.toByteArray(zis);
        }

    }

    @Test
    public void listFiles() {
        Set<String> properties = Config.getResources();
        assertThat(properties, hasItem("com/github/jhpoelen/fbob/osmose_config/maps/Amberjacks_1.csv"));
    }

    @Test
    public void zipFiles() throws IOException {
        Set<String> resources = Config.getResources();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Config.toZipOutputStream(resources, out);
        ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(out.toByteArray()));
        ZipEntry entry;
        List<String> entryNames = new ArrayList<>();
        while ((entry = inputStream.getNextEntry()) != null) {
            entryNames.add(entry.getName());
            IOUtils.copy(inputStream, new ByteArrayOutputStream());
        }


        assertThat(entryNames.size(), is(resources.size()));
    }

}
