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
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.fail;

public class ValueFactoryFishbaseCacheTest extends ValueFactoryFishbaseTestBase {

    ValueFactory createValueFactory() {
        return new ValueFactory() {

            private Map<URI, URI> remoteLocalURI = new HashMap<>();
            private Map<String, Map<String, String>> groupValueMap = new HashMap<>();

            @Override
            public String groupValueFor(String name, Group group) {
                try {
                    String s = IOUtils.toString(new URI("https://raw.githubusercontent.com/jhpoelen/fishbase_archiver/v0.2.0/table_names.tsv"));
                    String[] split = StringUtils.split(s, "\n");
                    final List<String> tables = Arrays.asList(split);

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
                                Map<String, String> valuesForSpecCodes = collectValuesForSpecCodes(columnName, remoteLocalURI.get(uri).toURL().openStream());
                                selectGroupValueUsingOrderedTaxonList(valuesForSpecCodes);
                            }
                        }

                        private void selectGroupValueUsingOrderedTaxonList(Map<String, String> valuesForSpecCodes) {
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

                        private Map<String, String> collectValuesForSpecCodes(String columnName, InputStream in) throws IOException {
                            Map<String, String> valuesForSpecCodes = new HashMap<>();

                            TsvParserSettings settings = new TsvParserSettings();
                            settings.getFormat().setLineSeparator("\n");
                            settings.setMaxCharsPerColumn(1024 * 1024);
                            settings.setHeaderExtractionEnabled(true);
                            TsvParser parser = new TsvParser(settings);
                            parser.beginParsing(new GZIPInputStream(in), "UTF-8");
                            Record record;
                            while ((record = parser.parseNextRecord()) != null) {
                                boolean hasSpecCode = record.getMetaData().containsColumn("SpecCode");
                                boolean hasRequestedColumn = record.getMetaData().containsColumn(columnName);
                                if (hasRequestedColumn && hasSpecCode) {
                                    String specCode = record.getString("SpecCode");
                                    if (specCodes.contains(specCode)) {
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
                } catch (IOException | URISyntaxException e) {
                    fail("unexpected io exception");
                }
                return groupValueMap.containsKey(group.getName())
                    ? groupValueMap.get(group.getName()).get(name)
                    : null;
            }

            private InputStream getMappingInputStream() {
                return getClass().getResourceAsStream(ValueFactoryFishbaseBase.FISHBASE_MAPPING_CSV);
            }
        };
    }

}
