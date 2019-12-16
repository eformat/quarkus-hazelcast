package org.acme;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.HazelcastInstance;

import javax.inject.Singleton;
import java.util.HashMap;

@Singleton
public class HazelcastBeanFactory extends HashMap<String, HazelcastInstance> {

    public void create(String ENABLE_MULTICAST, String PORT, String MULTICAST_GROUP, String SERVICE_DNS_NAME) {
        Config cfg = new Config();
        //cfg.setProperty("hazelcast.initial.min.cluster.size", CLUSTER_SIZE.toString());

        NetworkConfig network = cfg.getNetworkConfig();
        network.setPortAutoIncrement(true);
        network.setPort(new Integer(PORT));
        //network.getInterfaces().setEnabled(true).addInterface("192.168.*.*");

        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setMulticastGroup(MULTICAST_GROUP);

        // https://github.com/hazelcast/hazelcast-kubernetes#hazelcast-configuration-1
        if (ENABLE_MULTICAST.compareTo("false") == 0) {
            join.getMulticastConfig().setEnabled(false);
            join.getKubernetesConfig().setEnabled(true).setProperty("service-dns", SERVICE_DNS_NAME);
        }

        //join.getMulticastConfig().setMulticastTimeoutSeconds(60);

        HazelcastBean bean = new HazelcastBean();
        bean.create(cfg);
        System.out.println("debug: joined via " + join + " with " + bean.getInstance().getCluster()
                .getMembers().size() + " members.");
        this.put(bean.getName(), bean.getInstance());
        System.out.println("Instances created: " + this.size());
    }
}
