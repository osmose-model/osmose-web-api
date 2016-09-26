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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TraitFinder {
    public static Map<String, String> mapProperties(Map<String, String> jsonStrings, InputStream mappingInputStream) throws IOException {
        Map<String, String> speciesProperties = new HashMap<String, String>();

        final CSVReader reader = new CSVReader(new InputStreamReader(mappingInputStream), ',');
        String[] line;
        while ((line = reader.readNext()) != null) {
            final String fishbaseTableName = line[0];
            final String fishbaseColumnName = line[1];
            if (line.length > 3 && StringUtils.isNotBlank(fishbaseColumnName)) {
                final String propertyName = line[2];
                final String defaultValue = line[3];
                String value = valueFromTableResults(jsonStrings, fishbaseTableName, fishbaseColumnName);
                speciesProperties.put(propertyName, StringUtils.isBlank(value) ? defaultValue : value);
            }
        }


        return speciesProperties;
    }

    private static String valueFromTableResults(Map<String, String> resultJsons, String tableName, String columnName) throws IOException {
        String value = null;
        String jsonString = resultJsons.get(tableName);
        if (StringUtils.isNoneBlank(jsonString)) {
            final JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
            final JsonNode data = jsonNode.get("data");
            if (data != null && data.isArray() && data.size() > 0) {
                final JsonNode firstHit = data.get(0);
                final JsonNode trait = firstHit.get(columnName);
                if (trait != null && !trait.isNull()) {
                    value = trait.asText();
                }
            }
        }
        return value;
    }


    public static URI uriForTableQuery(String path, String query) throws URISyntaxException {
        return new URI("https", "fishbase.ropensci.org", path, query, null);
    }

    public static URI queryTable(Taxon taxon, String tableName) throws URISyntaxException {
        URI queryURI;
        if (StringUtils.startsWith(taxon.getUrl(), "http://fishbase.org/summary/")) {
            String queryString = StringUtils.replace(taxon.getUrl(), "http://fishbase.org/summary/", "SpecCode=");
            queryURI = uriForTableQuery(tableName, queryString);
        } else if (StringUtils.startsWith(taxon.getUrl(), "http://sealifebase.org/summary/")) {
            String queryString = StringUtils.replace(taxon.getUrl(), "http://sealifebase.org/summary/", "SpecCode=");
            queryURI = uriForTableQuery("/sealifebase" + tableName, queryString);
        } else {
            String queryString = queryForNameOnly(taxon.getName());
            queryURI = uriForTableQuery(tableName, queryString);
        }
        return queryURI;
    }

    public static String queryForNameOnly(String speciesName) {
        Pattern r = Pattern.compile("([A-Z][a-z]+)\\s*([A-Z|\\w]+)");
        Matcher m = r.matcher(speciesName);
        StringBuilder query = new StringBuilder();
        while (m.find()) {
            if (m.groupCount() > 0) {
                query.append("Genus=");
                query.append(StringUtils.capitalize(m.group(1)));
            }
            if (m.groupCount() > 1) {
                query.append("&Species=");
                query.append(StringUtils.lowerCase(m.group(2)));
            }
        }
        return query.toString();
    }

    public static Map<String, String> findTraits(Taxon taxon, InputStream fishbaseMapping) throws URISyntaxException, IOException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        List<String> tableNames = Arrays.asList("species", "popgrowth", "poplw", "maturity",
                "fecundity", "spawning", "estimate", "popll");
        // see issue https://github.com/jhpoelen/fb-osmose-bridge/issues/74
        //tableNames.add("popqb");
        Map<String, String> results = new TreeMap<>();
        for (String tableName : tableNames) {
            results.put(tableName, IOUtils.toString(queryTable(taxon, "/" + tableName), "UTF-8"));
        }
        speciesProperties.putAll(mapProperties(results, fishbaseMapping));

        return speciesProperties;
    }


}
