package com.github.jhpoelen.fbob;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TraitFinderIT {

    @Test
    public void findLifeSpanDynamic() throws IOException, URISyntaxException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        final String query = TraitFinderTest.queryForSpecies("ScomberomorusCavalla");
        if (StringUtils.isNotBlank(query)) {
            final URI uri = TraitFinderTest.uriForFishbaseSpeciesQuery(query);
            final String jsonString = IOUtils.toString(uri, "UTF-8");
            speciesProperties.putAll(TraitFinderTest.mapProperties(jsonString, getClass().getResourceAsStream("fishbase-mapping.csv")));
        }
        assertThat(speciesProperties.get("species.lifespan"), is("14.0"));
    }

}
