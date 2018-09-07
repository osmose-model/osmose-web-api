package fr.ird.osmose.web.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("v2/osmose_config.zip")
public class ConfigServiceV2  {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/zip")
    public Response configTemplateFromGroups(Config config) throws IOException {
        return ConfigServiceUtil.responseFor(ConfigServiceUtil.asStream(config, ConfigServiceUtil.getValueFactory(config.getGroups())));
    }

}
