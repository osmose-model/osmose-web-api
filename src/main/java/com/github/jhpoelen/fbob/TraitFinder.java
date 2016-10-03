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

    public static void doMapping(InputStream mappingInputStream, PropertyMapping mapper) throws IOException {
        final CSVReader reader = new CSVReader(new InputStreamReader(mappingInputStream), ',');
        String[] line;
        while ((line = reader.readNext()) != null) {
            final String tableName = line[0];
            final String columnName = line[1];
            if (line.length > 3 && StringUtils.isNotBlank(columnName)) {
                final String propertyName = line[2];
                final String defaultValue = line[3];
                mapper.forMapping(tableName, columnName, propertyName, defaultValue);
            }
        }
    }

    public static Map<String, String> mapProperties(Map<String, String> tableResultMap, InputStream mappingInputStream) throws IOException {
        Map<String, String> speciesProperties = new HashMap<String, String>();

        doMapping(mappingInputStream, (tableName, columnName, mappedName, defaultValue) -> {
            String value = valueFromTableResults(tableResultMap, tableName, columnName);
            if (StringUtils.isNoneBlank(value)) {
                speciesProperties.put(mappedName, value);
            }
        });

        return speciesProperties;
    }

    private static String valueFromTableResults(Map<String, String> resultJsons, String tableName, String columnName) throws IOException {
        String value = null;
        String jsonString = resultJsons.get(tableName);
        if (StringUtils.isNoneBlank(jsonString)) {
            final JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
            final JsonNode data = jsonNode.get("data");
            if (data != null && data.isArray()) {
                for (JsonNode row : data) {
                    final JsonNode trait = row.get(columnName);
                    if (trait != null && !trait.isNull()) {
                        value = trait.asText();
                        break;
                    }
                }

            }
        }
        return value;
    }


    public static URI uriForTableQuery(String path, String query) throws URISyntaxException {
        return new URI("https", "fishbase.ropensci.org", path, query, null);
    }

    public static List<URI> urisForTableDocs() throws URISyntaxException {
        return Arrays.asList(new URI("https", "fishbase.ropensci.org", "/docs", null, null),
                new URI("https", "fishbase.ropensci.org", "/sealifebase/docs", null, null));
    }

    public static URI queryTable(Taxon taxon, String tableName) throws URISyntaxException {
        URI queryURI;
        if (StringUtils.startsWith(taxon.getUrl(), "http://fishbase.org/summary/")) {
            String queryString = StringUtils.replace(taxon.getUrl(), "http://fishbase.org/summary/", "SpecCode=");
            queryURI = uriForTableQuery(tableName, queryString + "&limit=5000");
        } else if (StringUtils.startsWith(taxon.getUrl(), "http://sealifebase.org/summary/")) {
            String queryString = StringUtils.replace(taxon.getUrl(), "http://sealifebase.org/summary/", "SpecCode=");
            queryURI = uriForTableQuery("/sealifebase" + tableName, queryString + "&limit=5000");
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
        return findTraits(taxon, fishbaseMapping, findUsedTables());
    }

    protected static List<String> findUsedTables(InputStream mappingInputStream) throws IOException {
        Set<String> tableNames = new HashSet<>();
        doMapping(mappingInputStream, (tableName, columnName, mappedName, defaultValue) -> tableNames.add(tableName));
        return new ArrayList<>(tableNames);
    }

    public static Map<String, String> findTraits(Taxon taxon, InputStream fishbaseMapping, List<String> tableNames) throws URISyntaxException, IOException {
        Map<String, String> speciesProperties = new HashMap<String, String>();
        Map<String, String> results = new TreeMap<>();
        for (String tableName : tableNames) {
            try {
                results.put(tableName, IOUtils.toString(queryTable(taxon, "/" + tableName), "UTF-8"));
            } catch (IOException ex) {
                System.err.println("failed to retrieve trait for [" + taxon.getName() + "]:[" + taxon.getUrl() + "] in table [" + tableName + "]");
                ex.printStackTrace(System.err);
            }
        }
        speciesProperties.putAll(mapProperties(results, fishbaseMapping));
        return speciesProperties;
    }

    static Set<String> availableTables() throws URISyntaxException, IOException {
        List<URI> uris = urisForTableDocs();
        Set<String> tables = new HashSet<>();
        for (URI uri : uris) {
            final JsonNode jsonNode = new ObjectMapper().readTree(uri.toURL());
            JsonNode data = jsonNode.get("data");
            if (data != null && data.isArray()) {
                for (JsonNode table : data) {
                    if (table.has("table")) {
                        String tableName = table.get("table").asText();
                        if (StringUtils.isNotBlank(tableName)) {
                            tables.add(tableName);
                        }
                    }
                }
            }
        }
        return tables;
    }

    static List<String> findUsedTables() throws IOException, URISyntaxException {
        List<String> tables = findUsedTables(TraitFinder.class.getResourceAsStream("fishbase-mapping.csv"));
        tables.retainAll(availableTables());
        return tables;
    }
}
