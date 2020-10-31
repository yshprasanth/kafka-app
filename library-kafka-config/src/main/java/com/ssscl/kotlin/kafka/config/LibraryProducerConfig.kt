package com.ssscl.kotlin.kafka.config

import com.ssscl.kafka.common.Book
import com.ssscl.kafka.common.Library
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
@EntityScan(basePackages = ["com.ssscl.kotlin.kafka.config", "java.lang"])
@ComponentScan(basePackages = ["com.ssscl.kotlin.kafka.config", "java.lang"])
open class LibraryProducerConfig {

    @Autowired
    private lateinit var libraryTopicConfig: LibraryTopicConfig;

    fun producerFactory() : ProducerFactory<String, String> {
        val props : MutableMap<String, Any> = HashMap()
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer())
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer())
        return DefaultKafkaProducerFactory<String, String>(props)
    }

    @Bean
    open fun stringKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate<String, String>(producerFactory())
    }

    fun bookProducerFactory() : ProducerFactory<String, Book> {
        val props: MutableMap<String, Any> = HashMap()
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer())
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer<Book>())
        return DefaultKafkaProducerFactory<String, Book>(props, StringSerializer(), JsonSerializer<Book>())
    }

    @Bean()
    @Qualifier("bookKafkaTemplate1")
    open fun bookKafkaTemplate(): KafkaTemplate<String, Book> {
        return KafkaTemplate<String, Book>(bookProducerFactory())
    }

    fun libraryProducerFactory(): ProducerFactory<String, Library> {
        val props: MutableMap<String, Any> = HashMap()
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer())
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer<Library>())
        return DefaultKafkaProducerFactory<String, Library>(props, StringSerializer(), JsonSerializer<Library>())
    }

    @Bean
    open fun libraryKafkaTemplate(): KafkaTemplate<String, Library> {
        return KafkaTemplate<String, Library>(libraryProducerFactory())
    }
}