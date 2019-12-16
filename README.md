### quarkus-hazelcast

Creates a hazelcast cluster using beans, single or multiple jvm's.

#### Multicast discovery

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

If you wish to use multicast in an openshift project:
```
oc annotate netnamespace quarkus-hazelcast netnamespace.network.openshift.io/multicast-enabled=true
``` 

And set `-Denable.multicast.discovery=true` on deployment

#### Run locally using multicast

Debug locally
```
mvn -Dquarkus.http.port=8080 -Denable.multicast.discovery=true quarkus:dev -Ddebug=true
```

Run locally as fatjar
```
mvn package -DskipTests
java -Dquarkus.http.port=8080 -Denable.multicast.discovery=true -jar target/quarkus-hazelcast-1.0.0-SNAPSHOT-runner.jar
```

Add hazelcast member (keep hitting this endpoint to create more members)
```
http localhost:8080/create
```

#### Run in OpenShift using service discovery (non multicast)

For kubernetes we can use a headless service for discovery on port 5701. This does not need multicast or cluster api view privileges as per the other tow methods:

- https://github.com/hazelcast/hazelcast-kubernetes

```
oc new-project quarkus-hazelcast
oc new-build --binary --name=quarkus-hazelcast -l app=quarkus-hazelcast --strategy=docker
oc start-build quarkus-hazelcast --from-dir=. --follow
oc new-app quarkus-hazelcast

oc delete svc/quarkus-hazelcast

cat <<EOF | oc apply -f -
apiVersion: v1
kind: Service
metadata:
  name: quarkus-hazelcast
spec:
  type: ClusterIP
  clusterIP: None
  selector:
    app: quarkus-hazelcast
    deploymentconfig: quarkus-hazelcast
  ports:
  - name: hazelcast
    port: 5701
---
apiVersion: v1
kind: Service
metadata:
  labels:
    app: quarkus-hazelcast
  name: quarkus-hazelcast-http
spec:
  ports:
  - name: 8080-tcp
    port: 8080
    protocol: TCP
    targetPort: 8080
  selector:
    app: quarkus-hazelcast
    deploymentconfig: quarkus-hazelcast
  sessionAffinity: None
  type: ClusterIP
EOF

oc expose svc/quarkus-hazelcast-http

export HOST=http://$(oc get route quarkus-hazelcast-http --template='{{ .spec.host }}')

http $HOST/create
```

For single pod, `/create` will cache multiple members per jvm.

Scale pods >= 2 and they will now form cluster across and within pod members.

And `-Denable.multicast.discovery=false` (this is the default).
