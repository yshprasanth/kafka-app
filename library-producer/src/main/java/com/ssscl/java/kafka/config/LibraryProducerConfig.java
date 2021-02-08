package com.ssscl.java.kafka.config;

import com.ssscl.kafka.common.Author;
import com.ssscl.kafka.common.Book;
import com.ssscl.kafka.common.PublisherOrg;
import com.ssscl.kafka.common.serializer.AuthorKafkaSerializer;
import com.ssscl.kotlin.kafka.config.LibraryTopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
public class LibraryProducerConfig {

    @Autowired
    private LibraryTopicConfig libraryTopicConfig;

    public ProducerFactory<String, String> stringProducerFactory() {
        final Map<String, Object> props = new HashMap();
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, new StringSerializer());
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, new StringSerializer());
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }

    public ProducerFactory<String, Book> bookProducerFactory() {
        final Map<String, Object> props = new HashMap();
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, Book> bookKafkaTemplate() {
        final KafkaTemplate<String, Book> kafkaTemplate = new KafkaTemplate<>(bookProducerFactory());
        kafkaTemplate.setDefaultTopic(libraryTopicConfig.booksTopicName);
        return kafkaTemplate;
    }

    public ProducerFactory<String, PublisherOrg> publisherOrgProducerFactory() {
        final Map<String, Object> props = new HashMap();
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, PublisherOrg> publisherOrgKafkaTemplate() {
        final KafkaTemplate<String, PublisherOrg> kafkaTemplate = new KafkaTemplate<>(publisherOrgProducerFactory());
        kafkaTemplate.setDefaultTopic(libraryTopicConfig.publisherOrgTopicName);
        return kafkaTemplate;
    }

    public ProducerFactory<String, Author> authorProducerFactory() {
        final Map<String, Object> props = new HashMap();
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new AuthorKafkaSerializer());
    }

    @Bean
    public KafkaTemplate<String, Author> authorKafkaTemplate() {
        final KafkaTemplate<String, Author> kafkaTemplate = new KafkaTemplate<>(authorProducerFactory());
        kafkaTemplate.setDefaultTopic(libraryTopicConfig.authorTopicName);
        return kafkaTemplate;
    }
}
