package com.ssscl.kotlin.kafka

import com.ssscl.kafka.common.Author
import com.ssscl.kafka.common.Book
import com.ssscl.kafka.common.PublisherOrg
import com.ssscl.kotlin.kafka.messaging.AuthorMessageConsumer
import com.ssscl.kotlin.kafka.messaging.MessageConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import java.util.concurrent.TimeUnit

@SpringBootApplication
@ComponentScan(basePackages = ["com.ssscl.kotlin.kafka.config", "java.lang"])
class LibraryConsumerApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val context = SpringApplication.run(LibraryConsumerApplication::class.java, *args)
            val messageConsumer: MessageConsumer = context.getBean(MessageConsumer::class.java)
            val authorMessageConsumer: AuthorMessageConsumer = context.getBean(AuthorMessageConsumer::class.java)
//            messageConsumer.bookLatch.await(60, TimeUnit.SECONDS);
//            messageConsumer.publisherOrgLatch.await(60, TimeUnit.SECONDS);
//            messageConsumer.authorLatch.await(60, TimeUnit.SECONDS);
            context.close()
        }
    }

    @Bean
    fun messageConsumer(): MessageConsumer {
        return MessageConsumer()
    }

    @Bean
    fun authorMessageConsumer(@Autowired
                              @Qualifier("authorConsumerFactory")
                              authorConsumerFactory: ConsumerFactory<String, Author>): AuthorMessageConsumer {
        return AuthorMessageConsumer(authorConsumerFactory)
    }
}