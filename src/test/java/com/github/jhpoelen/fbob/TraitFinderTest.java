package com.github.jhpoelen.fbob;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TraitFinderTest {

    @Test
    public void buildQuery() throws URISyntaxException {
        String query = queryForSpecies("ScomberomorusCavalla");
        String expectedUrl = "Genus=Scomberomorus&Species=cavalla";
        assertThat(query, is(expectedUrl));
        URI uri = uriForFishbaseSpeciesQuery(query);
        assertThat(uri.toString(), is("https://fishbase.ropensci.org/species?Genus=Scomberomorus&Species=cavalla"));
    }


    @Test
    public void findLifeSpanStatic() throws IOException, URISyntaxException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        final String jsonString = IOUtils.toString(getClass().getResourceAsStream("ScomberomorusCavalla.json"), "UTF-8");
        speciesProperties.putAll(mapProperties(jsonString, getClass().getResourceAsStream("fishbase-mapping.csv")));
        assertThat(speciesProperties.get("species.lifespan"), is("14.0"));
    }


    public static Map<String, String> mapProperties(String jsonString, InputStream mappingInputStream) throws IOException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        final JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
        final JsonNode data = jsonNode.get("data");
        if (data != null && data.isArray() && data.size() > 0) {
            final JsonNode firstHit = data.get(0);
            final CSVReader reader = new CSVReader(new InputStreamReader(mappingInputStream), ',');
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length > 3) {
                    final JsonNode trait = firstHit.get(line[1]);
                    String value = null;
                    if (trait != null) {
                        value = trait.asText();
                    }
                    final String propertyName = line[2];
                    final String defaultValue = line[3];
                    speciesProperties.put(propertyName, value == null ? defaultValue : value);
                }
            }
        }
        return speciesProperties;
    }

    public static URI uriForFishbaseSpeciesQuery(String query) throws URISyntaxException {
        return new URI("https", "fishbase.ropensci.org", "/species", query, null);
    }

    public static String queryForSpecies(String htlGroupName) {
        Pattern r = Pattern.compile("([A-Z][a-z]+)([A-Z][a-z]+)");
        Matcher m = r.matcher(htlGroupName);
        StringBuilder query = new StringBuilder();
        while (m.find()) {
            if (m.groupCount() > 0) {
                query.append("Genus=");
                query.append(m.group(1));
            }
            if (m.groupCount() > 1) {
                query.append("&Species=");
                query.append(StringUtils.lowerCase(m.group(2)));
            }
        }
        return query.toString();
    }
}
