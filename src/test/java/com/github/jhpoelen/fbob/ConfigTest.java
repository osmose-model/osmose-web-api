package com.github.jhpoelen.fbob;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class ConfigTest {

    @Test
    public void archive() throws IOException {
        Response actual = new Config().configArchive();
        assertThat(actual, is(notNullValue()));
        assertThat(actual.hasEntity(), is(true));
        StreamingOutput os = (StreamingOutput) actual.getEntity();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        os.write(outputStream);
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        assertThat(zis, is(notNullValue()));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
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

        assertThat(entryNames, not(hasItem("fishbase-mapping.csv")));
        assertThat(entryNames, not(hasItem("")));
        assertThat(resources, hasItem("com/github/jhpoelen/fbob/fishbase-mapping.csv"));
        assertThat(entryNames.size(), is(resources.size() - 1));
    }

    @Test
    public void configForSimpleGroups() throws IOException {
        final StreamingOutput streamingOutput = Config.asStream(Arrays.asList("focalOne", "focalTwo"),
                Arrays.asList("backgroundOne", "backgroundTwo"),
                ConfigUtil.getDefaultValueFactory());

        configFileShouldBeGenerated(streamingOutput);
    }

    @Test
    public void configForGroups() throws IOException {
        Group focalOne = new Group("focalOne", GroupType.FOCAL);
        Group focalTwo = new Group("focalTwo", GroupType.FOCAL);
        Group backgroundOne = new Group("backgroundOne", GroupType.BACKGROUND);
        Group backgroundTwo = new Group("backgroundTwo", GroupType.BACKGROUND);
        final StreamingOutput streamingOutput = Config.asStream(Arrays.asList(focalOne, focalTwo, backgroundOne, backgroundTwo),
                ConfigUtil.getDefaultValueFactory());

        configFileShouldBeGenerated(streamingOutput);
    }

    public void configFileShouldBeGenerated(StreamingOutput streamingOutput) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        streamingOutput.write(os);
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(os.toByteArray()));
        ZipEntry entry;
        List<String> names = new ArrayList<>();
        while ((entry = zip.getNextEntry()) != null) {
            assertThat(names, not(hasItem(entry.getName())));
            names.add(entry.getName());
        }


        assertThat(names, hasItems(
                "osm_param-fishing.csv",
                "fishing/fishing-seasonality-focalOne.csv",
                "fishing/fishing-seasonality-focalTwo.csv",
                "osm_param-init-pop.csv",
                "grid-mask.csv",
                "osm_param-movement.csv",
                "maps/focalOne0.csv",
                "maps/focalTwo1.csv",
                "osm_param-natural-mortality.csv",
                "osm_param-output.csv",
                "osm_param-predation.csv",
                "predation-accessibility.csv",
                "osm_param-reproduction.csv",
                "reproduction-seasonality-sp0.csv",
                "reproduction-seasonality-sp1.csv",
                "osm_param-species.csv",
                "osm_param-starvation.csv",
                "osm_param-mpa.csv",
                "osm_param-ltl.csv",
                "osm_param-grid.csv",
                "osm_ltlbiomass.nc",
                "osm_all-parameters.csv"));
    }


}
