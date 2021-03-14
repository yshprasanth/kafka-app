package com.ssscl.java.kafka.config;

import com.ssscl.kafka.common.Author;
import com.ssscl.kafka.common.Book;
import com.ssscl.kafka.common.PublisherOrg;
import com.ssscl.kafka.common.serializer.AuthorKafkaSerializer;
import com.ssscl.kotlin.kafka.config.LibraryTopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.COMPRESSION_TYPE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.MAX_BLOCK_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION;
import static org.apache.kafka.clients.producer.ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
public class LibraryProducerConfig {

    @Value("${library.kafka.max.in.flight.requests.per.connection:1}")
    public int maxInFlightRequestsPerConnection;

    @Value("${library.kafka.enable.idempotence:true}")
    public boolean enableIdempotence;

    @Value("${library.kafka.acks:all}")
    public String acks;

    @Value("${library.kafka.request.timeout.ms:10000}")
    public int requestTimeoutMS;

    @Value("${library.kafka.delivery.timeout.ms:30000}")
    public int deliveryTimeoutMS;

    @Value("${library.kafka.retries:3}")
    public int retries;

    @Value("${library.kafka.batch-size:5}")
    public int batchSize;

    @Value("${library.kafka.linger.ms:10}")
    public int lingerMS;

    @Value("${library.kafka.max.block.ms:5000}")
    public int maxBlockMS;

    @Value("${library.kafka.compression.type:snappy}")
    public String compressionType;

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

    public ProducerFactory<String, Book> bookProducerFactoryWithRetry() {
        final Map<String, Object> props = new HashMap();
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        props.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequestsPerConnection);
        props.put(ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        props.put(ACKS_CONFIG, acks);
        props.put(REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMS);
        props.put(DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMS);
        props.put(RETRIES_CONFIG, retries);
        props.put(BATCH_SIZE_CONFIG, batchSize);
        props.put(LINGER_MS_CONFIG, lingerMS);
        props.put(MAX_BLOCK_MS_CONFIG, maxBlockMS);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, Book> bookKafkaTemplateWithRetry() {
        final KafkaTemplate<String, Book> kafkaTemplate = new KafkaTemplate<>(bookProducerFactoryWithRetry());
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

    public ProducerFactory<String, Author> authorProducerFactory(KafkaProperties kafkaProperties) {
        final Map<String, Object> props = new HashMap();
        props.putAll(kafkaProperties.buildProducerProperties());

        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, AuthorKafkaSerializer.class);

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new AuthorKafkaSerializer());
    }

    @Bean
    public KafkaTemplate<String, Author> authorKafkaTemplate(KafkaProperties kafkaProperties) {
        final KafkaTemplate<String, Author> kafkaTemplate = new KafkaTemplate<>(authorProducerFactory(kafkaProperties));
        kafkaTemplate.setDefaultTopic(libraryTopicConfig.authorTopicName);
        return kafkaTemplate;
    }

    public ProducerFactory<String, Author> authorProducerFactoryWithRetry(KafkaProperties kafkaProperties) {
        final Map<String, Object> props = new HashMap();
        props.putAll(kafkaProperties.buildProducerProperties());

        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, AuthorKafkaSerializer.class);

        props.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequestsPerConnection);
        props.put(ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        props.put(ACKS_CONFIG, acks);
        props.put(REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMS);
        props.put(DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMS);
        props.put(RETRIES_CONFIG, retries);
        props.put(BATCH_SIZE_CONFIG, batchSize);
        props.put(LINGER_MS_CONFIG, lingerMS);
        props.put(MAX_BLOCK_MS_CONFIG, maxBlockMS);
        props.put(COMPRESSION_TYPE_CONFIG, compressionType);

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new AuthorKafkaSerializer());
    }

    @Bean
    public KafkaTemplate<String, Author> authorKafkaTemplateWithRetry(KafkaProperties kafkaProperties) {
        final KafkaTemplate<String, Author> kafkaTemplate = new KafkaTemplate<>(authorProducerFactoryWithRetry(kafkaProperties));
        kafkaTemplate.setDefaultTopic(libraryTopicConfig.authorTopicName);
        return kafkaTemplate;
    }
}
