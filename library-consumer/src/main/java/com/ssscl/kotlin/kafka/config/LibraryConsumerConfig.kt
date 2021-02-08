package com.ssscl.kotlin.kafka.config

import com.ssscl.kafka.common.Author
import com.ssscl.kafka.common.Book
import com.ssscl.kafka.common.PublisherOrg
import com.ssscl.kafka.common.serializer.AuthorKafkaDeserializer
import com.ssscl.kafka.common.serializer.AuthorKafkaSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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

    @Value(value = "\${library.kafka.books.consumer.group.id}")
    lateinit var booksConsumerGroupId: String;

    @Value(value = "\${library.kafka.publisher-org.consumer.group.id}")
    lateinit var publisherOrgConsumerGroupId: String;

    @Value(value = "\${library.kafka.authors.consumer.group.id}")
    lateinit var authorConsumerGroupId: String;

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
        props.put(GROUP_ID_CONFIG, booksConsumerGroupId)
        return DefaultKafkaConsumerFactory<String, Book>(props, StringDeserializer(), JsonDeserializer<Book>(Book::class.java))
    }

    @Bean
    fun bookKafkaListenerContainerFactory() : ConcurrentKafkaListenerContainerFactory<String, Book> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, Book> = ConcurrentKafkaListenerContainerFactory();
        factory.consumerFactory = bookConsumerFactory()
        return factory
    }

    private fun publisherOrgConsumerFactory(): ConsumerFactory<String, PublisherOrg> {
        val props : MutableMap<String, Any> = HashMap()
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        props.put(GROUP_ID_CONFIG, publisherOrgConsumerGroupId)
        return DefaultKafkaConsumerFactory<String, PublisherOrg>(props, StringDeserializer(), JsonDeserializer<PublisherOrg>(PublisherOrg::class.java))
    }

    @Bean
    fun publisherOrgKafkaListenerContainerFactory() : ConcurrentKafkaListenerContainerFactory<String, PublisherOrg> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, PublisherOrg> = ConcurrentKafkaListenerContainerFactory();
        factory.consumerFactory = publisherOrgConsumerFactory()
        return factory
    }

    private fun authorOrgConsumerFactory(): ConsumerFactory<String, Author> {
        val props : MutableMap<String, Any> = HashMap()
        props.put(BOOTSTRAP_SERVERS_CONFIG, libraryTopicConfig.bootstrapServer)
        props.put(GROUP_ID_CONFIG, authorConsumerGroupId)
        return DefaultKafkaConsumerFactory<String, Author>(props, StringDeserializer(), AuthorKafkaDeserializer())
    }

    @Bean
    fun authorKafkaListenerContainerFactory() : ConcurrentKafkaListenerContainerFactory<String, Author> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, Author> = ConcurrentKafkaListenerContainerFactory();
        factory.consumerFactory = authorOrgConsumerFactory()
        return factory
    }
}