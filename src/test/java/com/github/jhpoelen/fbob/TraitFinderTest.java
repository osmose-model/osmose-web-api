package com.github.jhpoelen.fbob;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TraitFinderTest {

    @Test
    public void buildQuery() throws URISyntaxException {
        String query = TraitFinder.queryForSpecies("ScomberomorusCavalla");
        String expectedUrl = "Genus=Scomberomorus&Species=cavalla";
        assertThat(query, is(expectedUrl));
        URI uri = TraitFinder.uriForFishbaseSpeciesQuery(query);
        assertThat(uri.toString(), is("https://fishbase.ropensci.org/species?Genus=Scomberomorus&Species=cavalla"));
    }


    @Test
    public void findLifeSpanStatic() throws IOException, URISyntaxException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        final String jsonString = IOUtils.toString(getClass().getResourceAsStream("ScomberomorusCavalla.json"), "UTF-8");
        speciesProperties.putAll(TraitFinder.mapProperties(jsonString, getClass().getResourceAsStream("fishbase-mapping.csv")));
        assertThat(speciesProperties.get("species.lifespan.sp"), is("14.0"));
    }


}
