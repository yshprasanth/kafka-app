package com.ssscl.kotlin.kafka.config

import com.ssscl.kafka.common.Book
import com.ssscl.kafka.common.Library
import org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import java.util.HashMap

@EnableKafka
@Configuration
open class LibraryConsumerConfig  {

    @Autowired
    lateinit var libraryTopicConfig: LibraryTopicConfig

    private fun stringConsumerFactory(groupId: String): ConsumerFactory<String, String> {
        val props: MutableMap<String, Any> = HashMap();
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        props.put(GROUP_ID_CONFIG, groupId)
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        return DefaultKafkaConsumerFactory(props);
    }

    @Bean
    fun stringKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, String> =  ConcurrentKafkaListenerContainerFactory();
        factory.consumerFactory = stringConsumerFactory("string")
        return factory
    }

    private fun bookConsumerFactory(): ConsumerFactory<String, Book> {
        val props : MutableMap<String, Any> = HashMap()
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        props.put(GROUP_ID_CONFIG, "book")
        return DefaultKafkaConsumerFactory<String, Book>(props, StringDeserializer(), JsonDeserializer<Book>(Book::class.java))
    }

    @Bean
    fun bookKafkaListenerContainerFactory() : ConcurrentKafkaListenerContainerFactory<String, Book> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, Book> = ConcurrentKafkaListenerContainerFactory();
        factory.consumerFactory = bookConsumerFactory()
        return factory
    }

    private fun libraryConsumerFactory(): ConsumerFactory<String, Library> {
        val props : MutableMap<String, Any> = HashMap()
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        props.put(GROUP_ID_CONFIG, "library")
        return DefaultKafkaConsumerFactory<String, Library>(props, StringDeserializer(), JsonDeserializer<Library>(Library::class.java))
    }

    @Bean
    fun libraryKafkaListenerContainerFactory() : ConcurrentKafkaListenerContainerFactory<String, Library> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, Library> = ConcurrentKafkaListenerContainerFactory();
        factory.consumerFactory = libraryConsumerFactory()
        return factory
    }
}