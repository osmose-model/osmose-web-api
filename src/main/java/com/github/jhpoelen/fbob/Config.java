package com.github.jhpoelen.fbob;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @GET
    @Produces("application/zip")
    public Response configTemplate(@QueryParam("htlGroupName") final List<String> htlGroupNames) throws IOException {
        Response response;
        if (htlGroupNames == null || htlGroupNames.size() == 0) {
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
            response = Response
                    .ok(asStream(htlGroupNames, ltlGroupNames))
                    .header("Content-Disposition", "attachment; filename=osmose_config.zip")
                    .build();
        }
        return response;
    }

    public static StreamingOutput asStream(final List<String> groupNames, final List<String> implicitGroupNames) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                ZipOutputStream zos = new ZipOutputStream(os);
                ConfigUtil.generateConfigFor(groupNames, implicitGroupNames, new StreamFactory() {
                    @Override
                    public OutputStream outputStreamFor(String name) throws IOException {
                        ZipEntry e = new ZipEntry(name);
                        zos.putNextEntry(e);
                        return zos;
                    }
                });
                close(zos);
            }
        };
    }

    private static void close(ZipOutputStream zos) throws IOException {
        zos.closeEntry();
        zos.flush();
        zos.close();
    }

}
