package com.github.jhpoelen.fbob;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.stream.Stream.*;

@Path("osmose_config.zip")
public class Config {

    public static final String OSMOSE_CONFIG = "osmose_config";

    static public Set<String> getResources() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("com.github.jhpoelen.fbob." + OSMOSE_CONFIG))
                .setScanners(new ResourcesScanner()));
        return reflections.getResources(Pattern.compile(".*\\.csv"));
    }

    public static void toZipOutputStream(Set<String> resources, OutputStream out) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(out);
        for (String resource : resources) {
            String resourceName = StringUtils.substringAfter(resource, OSMOSE_CONFIG + "/");
            ZipEntry e = new ZipEntry(resourceName);
            zos.putNextEntry(e);
            IOUtils.write(IOUtils.toByteArray(Config.class.getResourceAsStream("/" + resource)), zos);
        }
        close(zos);
    }

    public Response configArchive() {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                toZipOutputStream(getResources(), os);
            }
        };

        return Response
                .ok(stream)
                .header("Content-Disposition", "attachment; filename=osmose_config.zip")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/zip")
    public Response configTemplateFromGroups(List<Group> groups) throws IOException {
        if (groups.size() == 0) {
            throw new IOException("expect at least [1] group, but got [" + groups.size() + "]");
        }
        return responseFor(asStream(groups, getValueFactory()));
    }

    @GET
    @Produces("application/zip")
    public Response configTemplate(@QueryParam("focalGroupNames") final List<String> focalGroupNames) throws IOException {
        Response response;
        if (focalGroupNames == null || focalGroupNames.size() == 0) {
            response = configArchive();
        } else {
            List<String> ltlGroupNames = Arrays.asList(
                    "SmallPhytoplankton",
                    "Diatoms",
                    "Microzooplankton",
                    "Mesozooplankton",
                    "Meiofauna",
                    "SmallInfauna",
                    "SmallMobileEpifauna",
                    "Bivalves",
                    "EchinodermsAndLargeGastropods"
            );

            response = responseFor(asStream(focalGroupNames, ltlGroupNames, getValueFactory()));
        }
        return response;
    }

    public Response responseFor(StreamingOutput os) {
        Response response;
        response = Response
                .ok(os)
                .header("Content-Disposition", "attachment; filename=osmose_config.zip")
                .build();
        return response;
    }

    public ValueFactory getValueFactory() {
        final List<ValueFactory> valueFactories = Arrays.asList(
                ConfigUtil.getFishbaseValueFactory(),
                ConfigUtil.getDefaultValueFactory());
        return ConfigUtil.getProxyValueFactory(valueFactories);
    }

    public static StreamingOutput asStream(final List<String> focalGroupNames, final List<String> backgroundGroupNames, final ValueFactory valueFactory) {
        final Stream<Group> groups = concat(asGroups(focalGroupNames, GroupType.FOCAL), asGroups(backgroundGroupNames, GroupType.BACKGROUND));
        return asStream(groups.collect(Collectors.toList()), valueFactory);
    }

    public static StreamingOutput asStream(List<Group> groups, final ValueFactory valueFactory) {
        final List<Group> focalGroups = groups.stream().filter(g -> GroupType.FOCAL == g.getType()).collect(Collectors.toList());
        final List<Group> backgroundGroups = groups.stream().filter(g -> GroupType.BACKGROUND == g.getType()).collect(Collectors.toList());

        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                ZipOutputStream zos = new ZipOutputStream(os);
                ConfigUtil.generateConfigFor(focalGroups, backgroundGroups, new StreamFactory() {
                    @Override
                    public OutputStream outputStreamFor(String name) throws IOException {
                        ZipEntry e = new ZipEntry(name);
                        zos.putNextEntry(e);
                        return zos;
                    }
                }, valueFactory);
                close(zos);
            }
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

}
