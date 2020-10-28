package com.ssscl.kotlin.kafka

import com.ssscl.kafka.common.Book
import com.ssscl.kafka.common.Library
import com.ssscl.kotlin.kafka.messaging.MessageConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.kafka.core.KafkaTemplate
import java.util.concurrent.TimeUnit

@SpringBootApplication
object KafkaApplication {

    @Value(value = "\${book.topic.name}")
    private val bookTopicName: String? = null

    @Value(value = "\${library.topic.name}")
    private val libraryTopicName: String? = null

    @Autowired
    private val bookKafkaTemplate: KafkaTemplate<String, Book>? = null

    @Autowired
    private val libraryKafkaTemplate: KafkaTemplate<String, Library>? = null

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val context = SpringApplication.run(KafkaApplication::class.java, *args)
        val messageConsumer: MessageConsumer = context.getBean(MessageConsumer::class.java)
        messageConsumer.bookLatch.await(10, TimeUnit.SECONDS);
        messageConsumer.libraryLatch.await(10, TimeUnit.SECONDS);
        context.close()
    }
}