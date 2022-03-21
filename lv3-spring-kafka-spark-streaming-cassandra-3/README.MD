https://github.com/VMTL/KafkaSpark/tree/master/src/main/java/app

# Java Kafka - Spark streaming  
This is a sample of a Kafka - Spark streaming application for a near real-time news processing

## Tools:

Kafka
Spark
Cassandra

## Prerequisites:
Locally installed Kafka, Zookeeper (I am using pre-installed Zookeeper version in Kafka), Cassandra

## Runnig the application:
Run Zookeeper - Kafka
```

CONFIGs
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

TERMINAL 1:KAFKA 
cd $KAFKA_HOME
cp config/server.properties config/server-1.properties
cp config/server.properties config/server-2.properties
cp config/server.properties config/server-3.properties
cd config
ls


Now edit these new files and set the following properties:
cd $KAFKA_HOME
cat config/server-1.properties
    broker.id=1
    listeners=PLAINTEXT://:9093
    log.dir=/tmp/kafka-logs-1

cd $KAFKA_HOME
cat config/server-2.properties
    broker.id=2
    listeners=PLAINTEXT://:9094
    log.dir=/tmp/kafka-logs-2

cd $KAFKA_HOME
cat config/server-3.properties
    broker.id=3
    listeners=PLAINTEXT://:9095
    log.dir=/tmp/kafka-logs-3

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 


- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
RUN AND TEST
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

TERMINAL 1:ZOOKEEPER
cd $KAFKA_HOME
bin/zookeeper-server-start.sh config/zookeeper.properties

TERMINAL 2:KAFKA - BROKER 1
cd $KAFKA_HOME
bin/kafka-server-start.sh config/server-1.properties
bin/kafka-server-stop.sh config/server-1.properties

TERMINAL 3:KAFKA - BROKER 2
cd $KAFKA_HOME
bin/kafka-server-start.sh config/server-2.properties
bin/kafka-server-stop.sh config/server-2.properties

TERMINAL 4:KAFKA - BROKER 3
cd $KAFKA_HOME
bin/kafka-server-start.sh config/server-3.properties
bin/kafka-server-stop.sh config/server-3.properties

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

TERMINAL 5:KAFKA
cd $KAFKA_HOME
lsof -i:9093 | grep LISTEN
lsof -i:9094 | grep LISTEN
lsof -i:9095 | grep LISTEN

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

TERMINAL 6:KAFKA
cd $KAFKA_HOME
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 5 --topic NewsAPI
bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic NewsAPI

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

TERMINAL 7:KAFKA producer
cd $KAFKA_HOME
bin/kafka-console-producer.sh --broker-list localhost:9093 --topic NewsAPI
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

TERMINAL 8:KAFKA consumer
cd $KAFKA_HOME
bin/kafka-console-consumer.sh --bootstrap-server localhost:9093 --from-beginning --topic NewsAPI




```
Run Cassandra
```
cd $CASSANDRA_HOME
TERMINAL 1
---------
cd $CASSANDRA_HOME
./bin/cassandra -f 


TERMINAL 2
---------
cd $CASSANDRA_HOME
cqlsh




DROP KEYSPACE IF EXISTS newsparsing;

CREATE KEYSPACE newsparsing WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

DESCRIBE keyspaces;

USE newsparsing;

CREATE TABLE news_words (word TEXT PRIMARY KEY, occurences int);

SELECT * FROM news_words;



DROP KEYSPACE newsparsing;

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 


```
Run Application
```
mvn clean compile exec:java -Dexec.mainClass="app.Application"
```

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 








