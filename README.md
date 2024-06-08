# Docker multi-container environment with Nifi, Hadoop, Spark, Hive and Superset

## Quick Start

To deploy the Nifi-HDFS-Spark-Hive-Superset cluster, run:
```
  docker-compose up
```

`docker-compose` creates a docker network that can be found by running `docker network list`

Run `docker network inspect` on the network (e.g. `docker-hadoop-spark-hive_default`) to find the IP the hadoop interfaces are published on. Access these interfaces with the following URLs:

* Namenode: http://<dockerhadoop_IP_address>:9870/dfshealth.html#tab-overview
* History server: http://<dockerhadoop_IP_address>:8188/applicationhistory
* Datanode: http://<dockerhadoop_IP_address>:9864/
* Nodemanager: http://<dockerhadoop_IP_address>:8042/node
* Resource manager: http://<dockerhadoop_IP_address>:8088/
* Spark master: http://<dockerhadoop_IP_address>:8080/
* Spark worker: http://<dockerhadoop_IP_address>:8081/
* Hive: http://<dockerhadoop_IP_address>:10000

## Important note regarding Docker Desktop
Since Docker Desktop turned “Expose daemon on tcp://localhost:2375 without TLS” off by default there have been all kinds of connection problems running the complete docker-compose. Turning this option on again (Settings > General > Expose daemon on tcp://localhost:2375 without TLS) makes it all work. I’m still looking for a more secure solution to this.


## Quick Start HDFS


Go to the bash shell on the namenode with that same Container ID of the namenode.
```
  docker exec -it namenode bash
```


## Start Spark (Scala)

Go to http://<dockerhadoop_IP_address>:8080 or http://localhost:8080/ on your Docker host (laptop) to see the status of the Spark master.

Go to the command line of the Spark master and start spark-shell.
```
  docker exec -it spark-master bash
  
  spark/bin/spark-shell --master spark://spark-master:7077
```

Load Spark Job Main.scala 
```
  :load /spark/job/src/main/scala/Main.scala
```

To run job
```
  Main.main()
```

## Quick Start Hive

Go to the command line of the Hive server and start hiveserver2
```
  docker exec -it hive-server bash

  hiveserver2
```

Maybe a little check that something is listening on port 10000 now
```
  netstat -anp | grep 10000
tcp        0      0 0.0.0.0:10000           0.0.0.0:*               LISTEN      446/java
```

Okay. Beeline is the command line interface with Hive. Let's connect to hiveserver2 now.
```
  beeline -u jdbc:hive2://localhost:10000 -n root
  
  !connect jdbc:hive2://127.0.0.1:10000 scott tiger
```

Didn't expect to encounter scott/tiger again after my Oracle days. But there you have it. Definitely not a good idea to keep that user on production.

Not a lot of databases here yet.
```
  show databases;
  
+----------------+
| database_name  |
+----------------+
| default        |
+----------------+
1 row selected (0.335 seconds)
```

Let's change that.
```
  create database crashlogs;
  use crashlogs;
```

And let's create a table.
```
CREATE EXTERNAL TABLE IF NOT EXISTS crash_day(
    date_log DATE,
    application VARCHAR(50),
    crash_type VARCHAR(10),
    number_of_crashes INT,
    crashed_users INT,
    crashed_devices INT,
    number_of_issues INT
    )
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
location '/output/day/crash'
TBLPROPERTIES ("skip.header.line.count"="1");
```

And have a little select statement going.

```
  select application from crash_day limit 10;
```

See all the table in hive-command.txt