package fr.ird.osmose.web.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("ping")
public class Ping {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "pong";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Msg pingPost(Msg postMsg) {
        final Msg msg = new Msg();
        msg.setMsg("pong");
        msg.setType(Msg.Type.RESPONSE);
        return msg;
    }
}
