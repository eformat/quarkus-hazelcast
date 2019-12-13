### quarkus-hazelcast

Creates a hazelcast cluster using beans, single or multiple jvm's

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

Debug locally
```
mvn -Dquarkus.http.port=8081 quarkus:dev
```

Run locally
```
mvn package -DskipTests
java -Dquarkus.http.port=8080 -jar target/quarkus-hazelcast-1.0.0-SNAPSHOT-runner.jar
```

Add hazelcast member
```asciidoc
http localhost:8080/create
```

OpenShift
```
oc new-project quarkus-hazelcast
oc new-build --binary --name=quarkus-hazelcast -l app=quarkus-hazelcast --strategy=docker
oc start-build quarkus-hazelcast --from-dir=. --follow
oc new-app quarkus-hazelcast
oc expose svc/quarkus-hazelcast

export HOST=http://$(oc get route quarkus-hazelcast --template='{{ .spec.host }}')

http $HOST/create
```
