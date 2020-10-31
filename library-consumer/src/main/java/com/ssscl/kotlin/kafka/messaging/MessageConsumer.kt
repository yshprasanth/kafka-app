package com.ssscl.kotlin.kafka.messaging

import com.ssscl.kafka.common.Book
import com.ssscl.kafka.common.Library
import org.springframework.kafka.annotation.KafkaListener
import java.util.concurrent.CountDownLatch

class MessageConsumer {
    val libraryLatch = CountDownLatch(3)
    val bookLatch = CountDownLatch(1)


    @KafkaListener(topics = ["\${library.kafka.books.topic.name}"], containerFactory = "bookKafkaListenerContainerFactory")
    fun bookListener(book: Book) {
        System.out.println("Received book message: $book")
        bookLatch.countDown()
    }

    @KafkaListener(topics = ["\${library.kafka.library.topic.name}"], containerFactory = "libraryKafkaListenerContainerFactory")
    fun libraryListener(library: Library) {
        System.out.println("Received library message: $library")
        libraryLatch.countDown()
    }
}