# Spring Boot 3 and Infinispan 14

## Problem Statement
Some warning regarding `sub-optimal read performance` displayed on both Infinispan Server and Client.

## Versions
- Red Hat Data Grid 8.4.6 Server
- 14.0.21.Final-redhat-00001 for Client

## Cache
```xml
<?xml version="1.0"?>
<replicated-cache name="user-cache" mode="SYNC" remote-timeout="300000" statistics="true">
	<encoding>
		<key media-type="application/x-protostream"/>
		<value media-type="application/x-protostream"/>
	</encoding>
	<locking concurrency-level="1000" isolation="READ_COMMITTED" acquire-timeout="60000" striping="false"/>
	<transaction mode="NON_XA" auto-commit="true" stop-timeout="30000" locking="PESSIMISTIC" reaper-interval="30000" complete-timeout="60000" notifications="true" transaction-manager-lookup="org.infinispan.transaction.lookup.GenericTransactionManagerLookup"/>
	<memory storage="OFF_HEAP"/>
    
    <!-- set this to limit the default query size -->
    <query default-max-results="10000" />
	
    <indexing enabled="true" storage="filesystem" path="user-cache">
		<indexed-entities>
			<indexed-entity>user.User</indexed-entity>
		</indexed-entities>
	</indexing>
	<state-transfer timeout="300000"/>
</replicated-cache>
```


## Infinispan Server 14 Logs
```
2023-12-19 13:29:09,373 INFO  (main) [BOOT] JVM Java HotSpot(TM) 64-Bit Server VM Oracle Corporation 20.0.1+9-jvmci-23.0-b12
2023-12-19 13:29:09,380 INFO  (main) [BOOT] JVM arguments = [-server, --add-exports, java.naming/com.sun.jndi.ldap=ALL-UNNAMED, --add-opens, java.base/java.util=ALL-UNNAMED, --add-opens, java.base/java.util.concurrent=ALL-UNNAMED, -Xlog:gc*:file=/Users/edwin/Documents/redhat-datagrid-8.4.6-server/server/log/gc.log:time,uptimemillis:filecount=5,filesize=3M, -Xms64m, -Xmx512m, -XX:MetaspaceSize=64M, -Djava.net.preferIPv4Stack=true, -Djava.awt.headless=true, -Dvisualvm.display.name=redhat-datagrid-server, -Djava.util.logging.manager=org.infinispan.server.loader.LogManager, -Dinfinispan.server.home.path=/Users/edwin/Documents/redhat-datagrid-8.4.6-server, -classpath, :/Users/edwin/Documents/redhat-datagrid-8.4.6-server/boot/infinispan-server-runtime-14.0.21.Final-redhat-00001-loader.jar, org.infinispan.server.loader.Loader, org.infinispan.server.Bootstrap, -c, infinispan-backup.xml]
2023-12-19 13:29:09,383 INFO  (main) [BOOT] PID = 50306
2023-12-19 13:29:09,411 INFO  (main) [org.infinispan.SERVER] ISPN080000: Red Hat Data Grid Server 8.4.6.GA starting

.....

2023-12-19 14:04:47,554 WARN  (blocking-thread-node1-p3-t3) [org.infinispan.protostream.impl.ProtoStreamReaderImpl] IPROTO000001: Field org.infinispan.query.remote.client.QueryRequest.namedParameters was read out of sequence leading to sub-optimal performance
2023-12-19 14:04:47,555 WARN  (blocking-thread-node1-p3-t3) [org.infinispan.protostream.impl.ProtoStreamReaderImpl] IPROTO000001: Field org.infinispan.query.remote.client.QueryRequest.local was read out of sequence leading to sub-optimal performance

```

## Spring Boot Client Logs
```
19-12-2023 14:04:48 [http-nio-8080-exec-3] WARN  org.infinispan.protostream.impl.ProtoStreamWriterImpl.checkRepeatedFieldWrite - IPROTO000002: Field org.infinispan.query.remote.client.QueryRequest.namedParameters was written out of sequence and will lead to sub-optimal read performance
19-12-2023 14:04:48 [http-nio-8080-exec-3] WARN  org.infinispan.protostream.impl.ProtoStreamWriterImpl.checkFieldWrite - IPROTO000002: Field org.infinispan.query.remote.client.QueryRequest.local was written out of sequence and will lead to sub-optimal read performance
```

## Generate Data
```
$ curl -kv http://localhost:8080/generate
```

## Querying
```
$ curl -kv http://localhost:8080/get-users-from-city?address=Jakarta
```