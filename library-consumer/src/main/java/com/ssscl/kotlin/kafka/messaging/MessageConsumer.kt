package com.ssscl.kotlin.kafka.messaging

import com.ssscl.kafka.common.Author
import com.ssscl.kafka.common.Book
import com.ssscl.kafka.common.PublisherOrg
import org.springframework.kafka.annotation.KafkaListener
import java.util.concurrent.CountDownLatch

class MessageConsumer {
    val publisherOrgLatch = CountDownLatch(300)
    val bookLatch = CountDownLatch(1)
    val authorLatch = CountDownLatch(1)

    @KafkaListener(topics = ["\${library.kafka.books.topic.name}"], containerFactory = "bookKafkaListenerContainerFactory")
    fun bookListener(book: Book) {
        System.out.println("Received book message: $book")
        bookLatch.countDown()
    }

    @KafkaListener(topics = ["\${library.kafka.publisher-org.topic.name}"], containerFactory = "publisherOrgKafkaListenerContainerFactory")
    fun publisherOrgListener(publisherOrg: PublisherOrg) {
        System.out.println("Received publisherOrg message: $publisherOrg")
        publisherOrgLatch.countDown()
    }

    @KafkaListener(topics = ["\${library.kafka.authors.topic.name}"], containerFactory = "authorKafkaListenerContainerFactory")
    fun authorListener(author: Author) {
        System.out.println("Received publisherOrg message: $author")
        authorLatch.countDown()
    }
}