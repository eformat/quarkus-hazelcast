package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/create")
public class APIResource {

    final HazelcastBeanFactory hazelcastBeanFactory = new HazelcastBeanFactory();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String create() {
        hazelcastBeanFactory.create();
        return "created";
    }
}
