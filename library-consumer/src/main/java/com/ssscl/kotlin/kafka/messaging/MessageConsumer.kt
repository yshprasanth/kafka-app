package com.ssscl.kotlin.kafka.messaging

import com.ssscl.kafka.common.Author
import com.ssscl.kafka.common.Book
import com.ssscl.kafka.common.PublisherOrg
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import java.util.concurrent.CountDownLatch

class MessageConsumer() {

//    val publisherOrgLatch = CountDownLatch(300)
//    val bookLatch = CountDownLatch(1)
//    val authorLatch = CountDownLatch(1)

//    @KafkaListener(topics = ["\${library.kafka.books.topic.name}"], containerFactory = "bookKafkaListenerContainerFactory")
//    fun bookListener(book: Book) {
//        System.out.println("Received book message: $book")
////        bookLatch.countDown()
//    }

//    @KafkaListener(topics = ["\${library.kafka.publisher-org.topic.name}"], containerFactory = "publisherOrgKafkaListenerContainerFactory")
//    fun publisherOrgListener(publisherOrg: PublisherOrg) {
//        System.out.println("Received publisherOrg message: $publisherOrg")
////        publisherOrgLatch.countDown()
//    }

//    @KafkaListener(topics = ["\${library.kafka.authors.topic.name}"],
//            containerFactory = "authorConcurrentKafkaListenerContainerFactory")
//    fun authorListener(author: Author) {
//        System.out.println("Received Author message: $author")
//        authorLatch.countDown()
//    }

//    @KafkaListener(topics = ["\${library.kafka.authors.topic.name}"])
//    fun singleMessageAuthorListener(author: Author,
//                                    ack: Acknowledgment,
//                                    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partition: Int,
//                                    @Header(KafkaHeaders.OFFSET) offset: Int) {
//        System.out.println("Received Author message: $author, $ack, $partition, $offset")
//        ack.acknowledge()
//    }
}