package com.ssscl.kotlin.kafka.config

import org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin
import java.util.HashMap;


@Configuration
open class LibraryTopicConfig {

    @Value(value = "\${library.kafka.bootstrap.server}")
    lateinit var bootstrapServer: String;

    @Value(value = "\${library.kafka.books.topic.name}")
    lateinit var booksTopicName: String;

    @Value(value = "\${library.kafka.publisher-org.topic.name}")
    lateinit var publisherOrgTopicName: String;

    @Value(value = "\${library.kafka.authors.topic.name}")
    lateinit var authorTopicName: String;

    companion object {
        val NUMBER_OF_PARTITIONS = 5;
        val NUMBER_OF_REPLICATION: Short = 3;
    }

    @Bean
    fun kafkaAdmin(): KafkaAdmin? {
        val configs: MutableMap<String, Any> = HashMap()
        configs.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        return KafkaAdmin(configs)
    }

    @Bean
    fun bookTopic(): NewTopic {
        return NewTopic(booksTopicName, NUMBER_OF_PARTITIONS, NUMBER_OF_REPLICATION)
    }

    @Bean
    fun publisherOrgTopic(): NewTopic {
        return NewTopic(publisherOrgTopicName, NUMBER_OF_PARTITIONS, NUMBER_OF_REPLICATION)
    }

    @Bean
    fun authorTopic(): NewTopic {
        return NewTopic(authorTopicName, NUMBER_OF_PARTITIONS, NUMBER_OF_REPLICATION)
    }
}