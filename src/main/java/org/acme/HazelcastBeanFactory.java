package org.acme;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.HazelcastInstance;

import javax.inject.Singleton;
import java.util.HashMap;

@Singleton
public class HazelcastBeanFactory extends HashMap<String, HazelcastInstance> {

    //private Integer CLUSTER_SIZE = 2;
    private int PORT = 5701;
    private String MULTICAST_GROUP = "224.0.0.1";

    public void create() {
        Config cfg = new Config();
        //cfg.setProperty("hazelcast.initial.min.cluster.size", CLUSTER_SIZE.toString());

        NetworkConfig network = cfg.getNetworkConfig();
        network.setPortAutoIncrement(true);
        network.setPort(PORT);
        //network.getInterfaces().setEnabled(true).addInterface("192.168.*.*");

        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setMulticastGroup(MULTICAST_GROUP);
        //join.getMulticastConfig().setEnabled(false);

        //join.getMulticastConfig().setMulticastTimeoutSeconds(60);

        HazelcastBean bean = new HazelcastBean();
        bean.create(cfg);
        System.out.println("debug: joined via " + join + " with " + bean.getInstance().getCluster()
                .getMembers().size() + " members.");
        this.put(bean.getName(), bean.getInstance());
        System.out.println("Instances created: " + this.size());
    }
}
