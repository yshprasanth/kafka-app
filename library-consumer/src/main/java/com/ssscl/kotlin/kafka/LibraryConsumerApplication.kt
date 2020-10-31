package com.ssscl.kotlin.kafka

import com.ssscl.kafka.common.Book
import com.ssscl.kafka.common.Library
import com.ssscl.kotlin.kafka.messaging.MessageConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.kafka.core.KafkaTemplate
import java.util.concurrent.TimeUnit

@SpringBootApplication
@EntityScan(basePackages = ["com.ssscl.kotlin.kafka.config", "java.lang"])
@ComponentScan(basePackages = ["com.ssscl.kotlin.kafka.config", "java.lang"])
class LibraryConsumerApplication {

    @Value(value = "\${library.kafka.books.topic.name}")
    private val bookTopicName: String? = null

    @Value(value = "\${library.kafka.library.topic.name}")
    private val libraryTopicName: String? = null

    @Autowired
    private val bookKafkaTemplate: KafkaTemplate<String, Book>? = null

    @Autowired
    private val libraryKafkaTemplate: KafkaTemplate<String, Library>? = null

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val context = SpringApplication.run(LibraryConsumerApplication::class.java, *args)
            val messageConsumer: MessageConsumer = context.getBean(MessageConsumer::class.java)
            messageConsumer.bookLatch.await(10, TimeUnit.SECONDS);
            messageConsumer.libraryLatch.await(10, TimeUnit.SECONDS);
            context.close()
        }
    }

    @Bean
    fun messageConsumer(): MessageConsumer {
        return MessageConsumer()
    }
}