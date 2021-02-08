package com.ssscl.java.kafka.messaging;

import com.ssscl.kafka.common.Author;
import com.ssscl.kafka.common.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@DependsOn("authorKafkaTemplate")
public class AuthorMessageProducer implements Runnable {

    @Autowired
    private KafkaTemplate<String, Author> authorKafkaTemplate;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final static String AUTHOR_NAME_PREFIX = "Mr.";
    private final static String AUTHOR_KEY_PREFIX = "Key_";
    private final static long THREAD_WAIT_TIME_MILLS = 2000l;

    public AuthorMessageProducer() {
    }

    public void start() {
        executorService.submit(this);
    }

    @Override
    public void run() {
        while(true) {
            final long currentMillis = System.currentTimeMillis();
            final String key = AUTHOR_KEY_PREFIX + currentMillis;

            final Author author = new Author();
            author.setFName(AUTHOR_NAME_PREFIX + currentMillis);

            final ListenableFuture<SendResult<String, Author>> future = authorKafkaTemplate.sendDefault(key, author);

            System.out.println("Sending message=[" + author + "]");
            future.addCallback(new ListenableFutureCallback<>() {
                public void onSuccess(SendResult<String, Author> result) {
                    System.out.println("After sending, onSuccess, message=[" + result.getProducerRecord().value() +
                            "] with result=[" + result.getRecordMetadata().topic() + ", " +
                            result.getRecordMetadata().offset() + "," + result.getRecordMetadata().partition() +
                            ", " + result.getRecordMetadata().timestamp() + "]");
                }

                public void onFailure(Throwable ex) {
                    System.out.println("After sending, onFailure, unable to send message=[" + author + "] due to : " + ex.getMessage());
                }
            });

            try {
                Thread.sleep(THREAD_WAIT_TIME_MILLS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        if(!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
