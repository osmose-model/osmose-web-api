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
import java.util.Arrays;
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
    private Map<String, Map<String, String>> groupValueMap = new HashMap<>();
    private List<String> tables = availableTables();

    @Override
    public String groupValueFor(String name, Group group) {
        try {

            final List<String> specCodes = group.getTaxa()
                .stream()
                .map(taxon -> StringUtils.replace(taxon.getUrl(), "http://fishbase.org/summary/", ""))
                .collect(Collectors.toList());

            PropertyMapper.doMapping(getMappingInputStream(), new PropertyMapping() {
                @Override
                public void forMapping(String tableName, String columnName, String mappedName, String defaultValue) throws IOException {
                    if (tables.contains(tableName) && StringUtils.equals(name, mappedName)) {
                        URI uri = URI.create("https://github.com/jhpoelen/fishbase_archiver/releases/download/v0.2.0/" + tableName + "_fishbase.tsv.gz");
                        if (!remoteLocalURI.containsKey(uri)) {
                            cache(uri);
                        }
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        Map<String, String> valuesForSpecCodes = collectValuesForSpecCodes(columnName, remoteLocalURI.get(uri).toURL().openStream(), specCodes);
                        selectGroupValueUsingOrderedTaxonList(valuesForSpecCodes, specCodes);
                        stopWatch.stop();
                        System.err.println("processed [" + tableName + ":" + columnName + "] in [" + stopWatch.getTime() + "] ms");

                    }
                }

                private void selectGroupValueUsingOrderedTaxonList(Map<String, String> valuesForSpecCodes, List<String> specCodeCandidates) {
                    if (!groupValueMap.containsKey(group.getName())) {
                        groupValueMap.put(group.getName(), new HashMap<>());
                    }
                    Map<String, String> valuesForGroup = groupValueMap.get(group.getName());

                    Iterator<String> iter = specCodeCandidates.iterator();
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
            LOG.log(Level.SEVERE, "failed to extract value [" + name + "] for group [" + group.getName() + "]", e);
        }
        return groupValueMap.containsKey(group.getName())
            ? groupValueMap.get(group.getName()).get(name)
            : null;
    }

    private static List<String> availableTables() {
        String s = "";
        try {
            s = IOUtils.toString(new URI("https://raw.githubusercontent.com/jhpoelen/fishbase_archiver/v0.2.0/table_names.tsv"));
        } catch (IOException | URISyntaxException e) {
            LOG.log(Level.SEVERE, "failed to retieve tables", e);
        }
        String[] split = StringUtils.split(s, "\n");
        return Arrays.asList(split);
    }

    private InputStream getMappingInputStream() {
        return getClass().getResourceAsStream(ValueFactoryFishbaseBase.FISHBASE_MAPPING_CSV);
    }
}
