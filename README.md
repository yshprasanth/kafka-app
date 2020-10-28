# Kafka-App
App with Kafka Message Publisher and Consumer

### ZooKeeper
Install ZooKeeper from https://zookeeper.apache.org/releases.html

### Kafka
Install Kafka from https://kafka.apache.org/downloads

### Configure Zookeeper and Kafka
1. Create data directory for ZooKeeper, i.e., ~/data/zookeeper
2. Update the data path in below locations:
    - _zookeeper-install-location_/conf/zoo.cfg
    - _kafka-install-location_/config/zookeeper.properties
    
### Single Node - Single Broker Configuration

#### Start Zookeeper
1. Execute below command in new terminal. Zookeeper starts on port 2181.
    - cd _kafka-install-location_
    - ./bin/zookeeper-server-start.sh config/zookeeper.properties

#### Start Kafka Server on single broker
1. Execute below command in new terminal. Kafka broker starts on port 9092.
    - cd _kafka-install-location_
    - ./bin/kafka-server-start.sh config/server.properties

#### Create a topic
1. Execute below command in new terminal
    - cd _kafka-install-location_
    - execute command "jps" to verify the Zookeeper and Kafka daemons are running
    - ./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic library-topic
    
2. See the newly created topic:
    - ./bin/kafka-topics.sh --list --zookeeper localhost:2181 (OR) ./bin/kafka-topics.sh --list --bootstrap-server localhost:9092
        - should see the output as: _library-topic_
    - ./bin/kafka-topics.sh --describe --zookeeper localhost:2181
        - should see the details of replicas and partitions of all topics
        
#### Publish a message
1. Execute below in new terminal
    - cd _kafka-install-location_
    - ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic library-topic
    - keep typing in the terminal to send messages
    
#### Consume message
1. Execute below in new terminal
    - cd _kafka-install-location_
    - ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic library-topic --from-beginning
    - receive the messages from the producer
    
### Single Node - Multiple Broker Configuration

#### Start Zookeeper
1. Execute below command in new terminal
    - cd _kafka-install-location_
    - ./bin/zookeeper-server-start.sh config/zookeeper.properties

#### Configure Kafka Server with 3 brokers
1. Execute below commands in new terminal
    - cd _kafka-install-location_/config
    - cp server.properties server-two.properties server-three.properties
    - vi server-two.properties
        - set below values:
            - broker.id=1
            - port=9093
            - logs.dir=/tmp/kafka-logs-1
    - vi server-three.properties
            - set below values:
                - broker.id=2
                - port=9094
                - logs.dir=/tmp/kafka-logs-2

#### Start Kafka Server on 3 brokers
1. Execute below commands in three new terminals
    - Terminal 1
        - cd _kafka-install-location_
        - ./bin/kafka-server-start.sh config/server.properties
    - Terminal 2
        - cd _kafka-install-location_
        - ./bin/kafka-server-start.sh config/server-two.properties
    - Terminal 3
        - cd _kafka-install-location_
        - ./bin/kafka-server-start.sh config/server-three.properties

#### Create a topic 
1. Execute below command in new terminal. Creates topic with 3 replicas.
    - cd _kafka-install-location_
    - execute command "jps" to verify the Zookeeper and 3 Kafka daemons are running
    - ./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic books-topic
    
2. See the newly created topic:
    - ./bin/kafka-topics.sh --list --zookeeper localhost:2181
        - should see the output as: _books-topic_
    - ./bin/kafka-topics.sh --describe --zookeeper localhost:2181
        - should see the details of replicas and partitions of all topics

#### Alter the topic 
1. Execute below command in new terminal. Alter the topic to have 4 partitions
    - cd _kafka-install-location_
    - execute command "jps" to verify the Zookeeper and 3 Kafka daemons are running
    - ./bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic books-topic --partitions 4 

#### Publish a message to first broker
1. Execute below in new terminal
    - cd _kafka-install-location_
    - ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic books-topic
        - keep typing in the terminal to send messages
    
#### Consume message from any of the three brokers
1. Execute below in new terminal
    - cd _kafka-install-location_
    - ./bin/kafka-console-consumer.sh --bootstrap-server localhost:_9092/9093/9094_ --topic books-topic --from-beginning
        - receive the messages from the producer