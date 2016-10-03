package com.github.jhpoelen.fbob;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TraitFinderTest {

    @Test
    public void buildQuery() throws URISyntaxException {
        assertKingMackerel(TraitFinder.queryForNameOnly("ScomberomorusCavalla"));
    }

    @Test
    public void buildQuery2() throws URISyntaxException {
        assertKingMackerel(TraitFinder.queryForNameOnly("Scomberomorus cavalla"));
    }

    @Test
    public void buildQueryByUrl() throws URISyntaxException {
        final Taxon taxon = new Taxon("scomberomorus  cavalla");
        taxon.setUrl("http://fishbase.org/summary/120");
        assertThat(TraitFinder.queryTable(taxon, "/species"), is(new URI("https://fishbase.ropensci.org/species?SpecCode=120&limit=5000")));
    }

    @Test
    public void buildQueryByUrl2() throws URISyntaxException {
        final Taxon taxon = new Taxon("donald duck");
        taxon.setUrl("http://sealifebase.org/summary/120");
        assertThat(TraitFinder.queryTable(taxon, "/species"), is(new URI("https://fishbase.ropensci.org/sealifebase/species?SpecCode=120&limit=5000")));
    }

    public void assertKingMackerel(String query) throws URISyntaxException {
        String expectedUrl = "Genus=Scomberomorus&Species=cavalla";
        assertThat(query, is(expectedUrl));
        URI uri = TraitFinder.uriForTableQuery("/species", query);
        assertThat(uri.toString(), is("https://fishbase.ropensci.org/species?Genus=Scomberomorus&Species=cavalla"));
    }


    @Test
    public void findLifeSpanStatic() throws IOException, URISyntaxException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        final String jsonString = IOUtils.toString(getClass().getResourceAsStream("ScomberomorusCavalla.json"), "UTF-8");
        TreeMap<String, String> resultMap = new TreeMap<String, String>() {{
            put("species", jsonString);
        }};
        speciesProperties.putAll(TraitFinder.mapProperties(resultMap, getClass().getResourceAsStream("fishbase-mapping.csv")));
        assertThat(speciesProperties.get("species.lifespan.sp"), is("14.0"));
    }


}
