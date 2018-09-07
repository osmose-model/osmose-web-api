package fr.ird.osmose.web.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Path("osmose_config.zip")
public class ConfigService {

    public static final String OSMOSE_CONFIG = "osmose_config";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/zip")
    public Response configTemplateFromGroups(List<Group> groups) throws IOException {
        StreamingOutput os = ConfigServiceUtil.asStream(groups, ConfigServiceUtil.getValueFactory(groups));
        return ConfigServiceUtil.responseFor(os);
    }

    @GET
    @Produces("application/zip")
    public Response configTemplate(@QueryParam("focalGroupName") final List<String> focalGroupNames) throws IOException {
        Response response;
        if (focalGroupNames == null || focalGroupNames.size() == 0) {
            response = ConfigServiceUtil.configArchive();
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

            List<Group> groups = toGroups(focalGroupNames, ltlGroupNames);
            response = ConfigServiceUtil.responseFor(ConfigServiceUtil.asStream(groups, ConfigServiceUtil.getValueFactory(groups)));
        }
        return response;
    }

    protected static List<Group> toGroups(List<String> focalGroupNames, List<String> ltlGroupNames) {
        return concat(
                    ConfigServiceUtil
                        .asGroups(focalGroupNames, GroupType.FOCAL),
                    ConfigServiceUtil
                        .asGroups(ltlGroupNames, GroupType.BACKGROUND))
                    .collect(Collectors.toList());
    }

}
