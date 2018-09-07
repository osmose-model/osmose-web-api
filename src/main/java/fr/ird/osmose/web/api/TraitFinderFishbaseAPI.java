package fr.ird.osmose.web.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.glassfish.grizzly.http.util.Header;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TraitFinderFishbaseAPI implements TraitFinder {

    private static final Logger LOG = Logger.getLogger(ConfigService.class.getName());


    private static CloseableHttpClient httpClient;

    public static Map<String, String> mapProperties(Map<String, String> tableResultMap, InputStream mappingInputStream) throws IOException {
        Map<String, String> speciesProperties = new HashMap<String, String>();

        PropertyMapper.doMapping(mappingInputStream, (tableName, columnName, mappedName, defaultValue) -> {
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
        return Arrays.asList(new URI("https", "fishbase.ropensci.org", "/docs/", null, null),
                new URI("https", "fishbase.ropensci.org", "/sealifebase/docs/", null, null));
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

    public static Map<String, String> findTraitsStatic(Taxon taxon, InputStream fishbaseMapping) throws URISyntaxException, IOException {
        return findTraitsStatic(taxon, fishbaseMapping, findUsedTablesStatic());
    }

    static List<String> findUsedTablesStatic() throws IOException, URISyntaxException {
        List<String> tables = findUsedTablesStatic(TraitFinderFishbaseAPI.class.getResourceAsStream("fishbase-mapping.csv"), new TreeSet<>());
        tables.retainAll(availableTablesStatic());
        return tables;
    }

    protected static List<String> findUsedTablesStatic(InputStream mappingInputStream, Collection<String> availableNames) throws IOException {
        Set<String> tableNames = new HashSet<>();
        PropertyMapper.doMapping(mappingInputStream, (tableName, columnName, mappedName, defaultValue) -> {
            if (!availableNames.contains(mappedName)) {
                tableNames.add(tableName);
            }
        });
        return new ArrayList<>(tableNames);
    }

    private static Map<String, String> findTraitsStatic(Taxon taxon, InputStream fishbaseMapping, Collection<String> tableNames) throws URISyntaxException, IOException {
        Map<String, String> speciesProperties = new HashMap<>();
        Map<String, String> results = new TreeMap<>();
        for (String tableName : tableNames) {
            try {
                URI uri = queryTable(taxon, "/" + tableName);
                System.out.print("[" + uri + "] processing...");
                HttpGet httpGet = new HttpGet(uri);
                httpGet.setHeader(Header.Accept.toString(), ContentType.APPLICATION_JSON.toString());
                HttpResponse resp = getHttpClient().execute(httpGet);
                int statusCode = resp.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    InputStream content = resp.getEntity().getContent();
                    results.put(tableName, IOUtils.toString(content, "UTF-8"));
                }
                EntityUtils.consume(resp.getEntity());
                System.out.println(" completed with code [" + statusCode + "].");
            } catch (IOException ex) {
                System.err.println("failed to retrieve trait for [" + taxon.getName() + "]:[" + taxon.getUrl() + "] in table [" + tableName + "]");
                ex.printStackTrace(System.err);
            }
        }
        speciesProperties.putAll(mapProperties(results, fishbaseMapping));
        return speciesProperties;
    }

    private static CloseableHttpClient createHttpClient(int soTimeoutMs) {
        RequestConfig config = RequestConfig.custom()
            .setSocketTimeout(soTimeoutMs)
            .setConnectTimeout(soTimeoutMs)
            .build();

        return HttpClientBuilder.create()
            .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
            .setServiceUnavailableRetryStrategy(new DefaultServiceUnavailableRetryStrategy(10, 5 * 1000))
            .setDefaultRequestConfig(config).build();
    }

    private static HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = createHttpClient(5 * 1000);
        }
        return httpClient;
    }

    static Set<String> availableTablesStatic() throws URISyntaxException, IOException {
        List<URI> uris = urisForTableDocs();
        Set<String> tables = new HashSet<>();
        for (URI uri : uris) {
            try {
                final JsonNode jsonNode = new ObjectMapper().readTree(IOUtils.toString(uri));
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
            } catch (IOException ex) {
                throw new IOException("failed to access [" + uri + "]", ex);
            }
        }
        return tables;
    }

    @Override
    public Map<String, String> findTraits(Taxon taxon, InputStream fishbaseMapping, List<String> tableNames) throws URISyntaxException, IOException {
        return TraitFinderFishbaseAPI.findTraitsStatic(taxon, fishbaseMapping, tableNames);
    }

    @Override
    public Collection<String> availableTables() {
        Set<String> tables = Collections.emptySet();
        try {
            tables = TraitFinderFishbaseAPI.availableTablesStatic();
        } catch (URISyntaxException | IOException e) {
            LOG.log(Level.SEVERE, "failed to retrieve available tables", e);
        }
        return tables;
    }

    @Override
    public Collection<String> findUsedTables() {
        List<String> tables = Collections.emptyList();
        try {
            tables = findUsedTablesStatic(TraitFinderFishbaseAPI.class.getResourceAsStream(ValueFactoryFishbaseBase.FISHBASE_MAPPING_CSV), new TreeSet<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tables.retainAll(availableTables());
        return tables;

    }
}
