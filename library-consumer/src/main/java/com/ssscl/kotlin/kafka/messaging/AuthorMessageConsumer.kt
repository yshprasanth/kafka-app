package com.ssscl.kotlin.kafka.messaging

import com.ssscl.kafka.common.Author
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.stereotype.Service
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class AuthorMessageConsumer(var authorConsumerFactory: ConsumerFactory<String, Author>) {

    @Value(value = "\${library.kafka.authors.consumer.auto.commit.interval.ms}")
    var authorKafkaPollingMS: Long = 0;

    @Value(value = "\${library.kafka.authors.consumer.enable.auto.commit}")
    var authorKafkaEnableAutoCommit: Boolean = false;

    @Value(value = "\${library.kafka.authors.topic.name}")
    lateinit var  authorKafkaTopicName: String

    var isRunning: AtomicBoolean = AtomicBoolean(true)

    var isPositionRecorded: AtomicBoolean = AtomicBoolean(false)

    var executorService: ExecutorService = Executors.newFixedThreadPool(1);

    var kafkaConsumer : Consumer<String, Author>? = null

    init {
        System.out.println("inside init..")
        executorService.submit(this.subscribe())
    }

    private fun subscribe(): Runnable {
        System.out.println("inside subscribe..")
      return Runnable {
          System.out.println("inside subscribe.Runnable, ${isRunning.get()}")
        if(isRunning.get()) {
            kafkaConsumer = this.authorConsumerFactory?.createConsumer()

            kafkaConsumer?.subscribe(listOf(authorKafkaTopicName))

            executorService.submit(this.consumeMessages())
        }
      }
    }

    private fun consumeMessages(): Runnable {
        System.out.println("inside consumeMessages..")
        return Runnable {
            System.out.println("inside consumeMessages.Runnable, ${isRunning.get()}")
            var records: ConsumerRecords<String, Author>?
            if (isPositionRecorded.get()) {
                records = kafkaConsumer?.poll(Duration.ofMillis(authorKafkaPollingMS))
            }

            while(isRunning.get()) {
                records= kafkaConsumer?.poll(Duration.ofMillis(authorKafkaPollingMS))

                records?.forEach {
                    System.out.println("received record: $it");
                    System.out.println("received author: ${it.value()}, ${it.offset()}")
                }

                if(records?.count()!! >0) {
                    if(!authorKafkaEnableAutoCommit) {
                        kafkaConsumer?.commitSync();
                    }
                }
            }

            kafkaConsumer?.unsubscribe();
            kafkaConsumer?.close()
        }
    }


}