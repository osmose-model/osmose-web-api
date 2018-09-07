package fr.ird.osmose.web.api;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConfigServiceUtil {
    private static final Logger LOG = Logger.getLogger(ConfigService.class.getName());

    static Set<String> getResources() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("com.github.jhpoelen.fbob." + ConfigService.OSMOSE_CONFIG))
                .setScanners(new ResourcesScanner()));
        return reflections.getResources(Pattern.compile(".*\\.csv"));
    }

    public static void toZipOutputStream(Set<String> resources, OutputStream out) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(out);
        for (String resource : resources) {
            String resourceName = StringUtils.substringAfter(resource, ConfigService.OSMOSE_CONFIG + "/");
            if (StringUtils.isNotBlank(resourceName)) {
                LOG.info("adding [" + resourceName + "]...");
                ZipEntry e = new ZipEntry(resourceName);
                zos.putNextEntry(e);
                IOUtils.write(IOUtils.toByteArray(ConfigService.class.getResourceAsStream("/" + resource)), zos);
                LOG.info("adding [" + resourceName + "] done.");
            }
        }
        close(zos);
        LOG.info("zipstream closed.");
    }

    public static Response responseFor(StreamingOutput os) {
        return Response
                .ok(os)
                .header("Content-Disposition", "attachment; filename=osmose_config.zip")
                .build();
    }

    public static StreamingOutput asStream(List<Group> groups, final ValueFactory valueFactory) {
        Config config = new Config();
        config.setGroups(groups);
        return asStream(config, valueFactory);
    }

    public static StreamingOutput asStream(final Config config, final ValueFactory valueFactory) {
        if (config.getGroups().size() == 0) {
            throw new IllegalArgumentException("expect at least [1] group, but got [" + config.getGroups().size() + "]");
        }

        System.err.println("configuration generating...");
        return os -> {
            ZipOutputStream zos = new ZipOutputStream(os);
            ConfigUtil.generateConfigFor(config, name -> {
                zos.flush();
                ZipEntry e = new ZipEntry(name);
                LOG.info("adding [" + name + "]...");
                zos.putNextEntry(e);
                LOG.info("adding [" + name + "] done.");
                zos.flush();
                return zos;
            }, valueFactory);
            close(zos);
        };

    }

    public static Stream<Group> asGroups(List<String> groupNames, GroupType type) {
        return groupNames
                .stream()
                .map(groupName -> new Group(groupName, type, Collections.singletonList(new Taxon(groupName))));
    }

    private static void close(ZipOutputStream zos) throws IOException {
        zos.closeEntry();
        zos.flush();
        zos.close();
    }

    public static Response configArchive() {
        StreamingOutput stream = os -> toZipOutputStream(getResources(), os);

        return Response
                .ok(stream)
                .header("Content-Disposition", "attachment; filename=osmose_config.zip")
                .build();
    }

    public static ValueFactory getValueFactory(List<Group> groups) {
        Stream<ValueFactoryCache> factories = Stream.of(ValueFactoryCache.Database.values())
                .flatMap(database -> {
                    ValueFactoryCache valueFactoryCache = new ValueFactoryCache(database, "v0.2.1-patch4") {{
                        setGroups(groups);
                    }};
                    List<String> tablesPatched = valueFactoryCache.getTables();
                    return Stream
                            .of(valueFactoryCache, new ValueFactoryCache(database, "v0.2.1", tablesPatched) {{
                                setGroups(groups);
                            }});
                });


        ValueFactory valueDefaults = ConfigUtil.getProxyValueFactory(
                Arrays.asList(
                        new ValueFactoryMappingDefaultsForGroup("fishbase-mapping-phytoplankton.csv", new Group("phytoplankton", GroupType.BACKGROUND)),
                        new ValueFactoryMappingDefaultsForGroup("fishbase-mapping-zooplankton.csv", new Group("zooplankton", GroupType.BACKGROUND)),
                        new ValueFactoryMappingDefault(),
                        new ValueFactoryDefault()
                )
        );

        ValueFactory valueOrDefault = ConfigUtil.getProxyValueFactory(
                Stream
                        .concat(factories, Stream.of(valueDefaults))
                        .collect(Collectors.toList()));

        final List<ValueFactory> valueFactories = Arrays.asList(
                new ValueFactoryCalculated(valueOrDefault),
                valueOrDefault);
        return new ValueFactoryNA(
                new ValueFactorySexRatioConstraints(
                        ConfigUtil.getProxyValueFactory(valueFactories),
                        valueDefaults));
    }
}
