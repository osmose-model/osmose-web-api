package com.github.jhpoelen.fbob;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

class ValueFactoryFishbaseCache implements ValueFactory {
    private static final Logger LOG = Logger.getLogger(ValueFactoryFishbaseCache.class.getName());
    public static final String COLUMN_NAME_SPEC_CODE = "SpecCode";
    private Map<URI, URI> remoteLocalURI = new HashMap<>();
    private Map<String, Map<String, String>> groupValueMap = null;

    private List<String> tables = null;
    private final List<String> tablesIgnored;
    private List<String> names = new ArrayList<>();
    private List<Group> groups = Collections.emptyList();
    private final String cacheVersion;

    public ValueFactoryFishbaseCache() {
        this("v0.2.1");
    }

    public ValueFactoryFishbaseCache(String cacheVersion) {
        this(cacheVersion, Collections.emptyList());
    }

    public ValueFactoryFishbaseCache(String cacheVersion, Collection<String> tablesIgnored) {
        this.cacheVersion = cacheVersion;
        this.tablesIgnored = new ArrayList<>(tablesIgnored);
    }

    private void init(String name) {
        if (null == tables) {
            this.tables = availableTables(getCacheURIPrefix());
        }
        if (groupValueMap == null) {
            groupValueMap = new HashMap<>();
        }
        names.add(name);
        try {
            final List<String> specCodesAll = groups.
                    stream()
                    .map(Group::getTaxa)
                    .flatMap(Collection::stream)
                    .map(taxon -> StringUtils.replace(taxon.getUrl(), "http://fishbase.org/summary/", ""))
                    .collect(Collectors.toList());

            PropertyMapper.doMapping(getMappingInputStream(), new PropertyMapping() {
                @Override
                public void forMapping(String tableName, String columnName, String mappedName, String defaultValue) throws IOException {
                    if (!tablesIgnored.contains(tableName)
                            && tables.contains(tableName)
                            && StringUtils.equals(name, mappedName)) {
                        URI uri = URI.create(getCacheURIPrefix() + "/" + tableName + "_fishbase.tsv.gz");
                        if (!remoteLocalURI.containsKey(uri)) {
                            cache(uri);
                        }
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        Map<String, String> valuesForSpecCodes = collectValuesForSpecCodes(columnName, remoteLocalURI.get(uri).toURL().openStream(), specCodesAll);
                        groups.forEach(group -> {
                            selectGroupValueUsingOrderedTaxonList(valuesForSpecCodes, group, mappedName);
                        });
                        stopWatch.stop();
                        System.err.println("processed [" + tableName + ":" + columnName + "] in [" + stopWatch.getTime() + "] ms");
                    }
                }

                private void selectGroupValueUsingOrderedTaxonList(Map<String, String> valuesForSpecCodes, Group group, String name) {
                    final List<String> specCodes = group.getTaxa()
                            .stream()
                            .map(taxon -> StringUtils.replace(taxon.getUrl(), "http://fishbase.org/summary/", ""))
                            .collect(Collectors.toList());

                    if (!groupValueMap.containsKey(group.getName())) {
                        groupValueMap.put(group.getName(), new HashMap<>());
                    }
                    Map<String, String> valuesForGroup = groupValueMap.get(group.getName());

                    Iterator<String> iter = specCodes.iterator();
                    while (iter.hasNext()
                            && !valuesForGroup.containsKey(name)) {
                        String v = valuesForSpecCodes.get(iter.next());
                        if (StringUtils.isNotBlank(v)) {
                            valuesForGroup.put(name, v);
                        }
                    }
                }

                private Map<String, String> collectValuesForSpecCodes(String columnName, InputStream in, List<String> specCodeCandidates) throws IOException {
                    Map<String, String> valuesForSpecCodes = new HashMap<>();

                    TsvParserSettings settings = new TsvParserSettings();
                    settings.getFormat().setLineSeparator("\n");
                    settings.setMaxCharsPerColumn(1024 * 1024);
                    settings.setHeaderExtractionEnabled(true);
                    TsvParser parser = new TsvParser(settings);
                    parser.beginParsing(new GZIPInputStream(in), "UTF-8");
                    Record record;
                    while ((record = parser.parseNextRecord()) != null) {
                        boolean hasSpecCode = record.getMetaData().containsColumn(COLUMN_NAME_SPEC_CODE);
                        boolean hasRequestedColumn = record.getMetaData().containsColumn(columnName);
                        if (hasRequestedColumn && hasSpecCode) {
                            String specCode = record.getString(COLUMN_NAME_SPEC_CODE);
                            if (specCodeCandidates.contains(specCode)) {
                                String value = record.getString(columnName);
                                if (StringUtils.isNotBlank(value) && !StringUtils.equals(value, "null")) {
                                    valuesForSpecCodes.put(specCode, StringUtils.trim(value));
                                }
                            }
                        }
                    }
                    parser.stopParsing();
                    return valuesForSpecCodes;
                }

                private void cache(URI uri) throws IOException {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    File tmpTable = File.createTempFile("fishbase", "tsv.gz");
                    tmpTable.deleteOnExit();
                    System.err.println("[" + uri.toString() + "] downloading...");
                    IOUtils.copyLarge(uri.toURL().openStream(), new FileOutputStream(tmpTable));
                    stopWatch.stop();
                    System.err.println("[" + uri.toString() + "] downloaded in [" + stopWatch.getTime() + "] ms.");
                    remoteLocalURI.put(uri, tmpTable.toURI());
                }
            });
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "failed to extract values for [" + StringUtils.join(names, ";") + "] for groups [" + StringUtils.join(groups.stream().map(group -> group.getName()).collect(Collectors.toList()), ";") + "]", e);
        }
    }

    private String getCacheURIPrefix() {
        return "https://github.com/jhpoelen/fishbase_archiver/releases/download/" + getCacheVersion();
    }

    public String getCacheVersion() {
        return cacheVersion;
    }

    @Override
    public String groupValueFor(String name, Group group) {
        if (groupValueMap == null || !names.contains(name)) {
            init(name);
        }
        return groupValueMap != null && groupValueMap.containsKey(group.getName())
                ? groupValueMap.get(group.getName()).get(name)
                : null;
    }

    private static List<String> availableTables(String prefix) {
        String s = "";
        try {
            s = IOUtils.toString(new URI(prefix + "/table_names.tsv"));
        } catch (IOException | URISyntaxException e) {
            LOG.log(Level.SEVERE, "failed to retieve tables", e);
        }
        String[] split = StringUtils.split(s, "\n");
        return Arrays.asList(split);
    }

    private InputStream getMappingInputStream() {
        return getClass().getResourceAsStream(ValueFactoryFishbaseBase.FISHBASE_MAPPING_CSV);
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }


}
