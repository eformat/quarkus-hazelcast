package org.acme;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/create")
public class APIResource {

    final HazelcastBeanFactory hazelcastBeanFactory = new HazelcastBeanFactory();

    @Inject
    @ConfigProperty(name = "enable.multicast.discovery", defaultValue = "false")
    String ENABLE_MULTICAST;

    @Inject
    @ConfigProperty(name = "discovery.port", defaultValue = "5701")
    String PORT;

    @Inject
    @ConfigProperty(name = "multicast.group", defaultValue = "224.0.0.1")
    String MULTICAST_GROUP;

    @Inject
    @ConfigProperty(name = "service.dns.name", defaultValue = "quarkus-hazelcast.quarkus-hazelcast.svc.cluster.local")
    String SERVICE_DNS_NAME;


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String create() {
        hazelcastBeanFactory.create(ENABLE_MULTICAST, PORT, MULTICAST_GROUP, SERVICE_DNS_NAME);
        return "created";
    }
}
