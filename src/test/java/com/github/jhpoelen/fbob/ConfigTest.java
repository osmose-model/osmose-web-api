package com.github.jhpoelen.fbob;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    public void archive() {
        assertThat(new Config().configArchive(), is(notNullValue()));
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
        ZipEntry entry = null;
        List<String> entryNames = new ArrayList<>();
        while ((entry = inputStream.getNextEntry()) != null) {
            entryNames.add(entry.getName());
        }


        assertThat(entryNames.size(), is(resources.size()));
    }

}
