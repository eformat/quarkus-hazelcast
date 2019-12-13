### quarkus-hazelcast

Check interface has multicast enabled and firewall accepts traffic
```
netstat -g -n | grep 224.0.0 | grep wlp2s0
suodo iptables -I INPUT -i wlp2s0 -d 224.0.0.1/32 -j ACCEPT
```

Hazelcast config
```
JoinConfig join = network.getJoin();
join.getMulticastConfig().setMulticastGroup(MULTICAST_GROUP);
```

Run
```
java -Dquarkus.http.port=8080 -jar target/quarkus-hazelcast-1.0.0-SNAPSHOT-runner.jar
```

Add hazelcast member
```asciidoc
http localhost:8080/create
```
