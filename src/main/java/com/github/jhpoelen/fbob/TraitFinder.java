package com.github.jhpoelen.fbob;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TraitFinder {
    public static Map<String, String> mapProperties(String jsonString, InputStream mappingInputStream) throws IOException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        final JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
        final JsonNode data = jsonNode.get("data");
        if (data != null && data.isArray() && data.size() > 0) {
            final JsonNode firstHit = data.get(0);
            final CSVReader reader = new CSVReader(new InputStreamReader(mappingInputStream), ',');
            String[] line;
            while ((line = reader.readNext()) != null) {
                final String fishbaseColumnName = line[1];
                if (line.length > 3 && StringUtils.isNotBlank(fishbaseColumnName)) {
                    final JsonNode trait = firstHit.get(fishbaseColumnName);
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

    public static Map<String, String> findTraitsForGroup(String groupName, InputStream fishbaseMapping) throws URISyntaxException, IOException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        final String query = queryForSpecies(groupName);
        if (StringUtils.isNotBlank(query)) {
            final URI uri = uriForFishbaseSpeciesQuery(query);
            final String jsonString = IOUtils.toString(uri, "UTF-8");
            speciesProperties.putAll(mapProperties(jsonString, fishbaseMapping));
        }
        return speciesProperties;
    }
}
