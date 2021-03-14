package com.ssscl.java.kafka.messaging;

import com.ssscl.kafka.common.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@DependsOn("bookKafkaTemplate")
public class BookMessageProducer implements Runnable {

    @Autowired
    private KafkaTemplate<String, Book> bookKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Book> bookKafkaTemplateWithRetry;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final static String BOOK_NAME_PREFIX = "MyBook_";
    private final static String BOOK_KEY_PREFIX = "Key_";
    private final static long THREAD_WAIT_TIME_MILLS = 2000l;

    public BookMessageProducer() {
    }

    public void start() {
        executorService.submit(this);
    }

    @Override
    public void run() {
        runWithRetry();
    }

    private void runWithoutRetry() {
        send(bookKafkaTemplate);
    }

    private void runWithRetry() {
        send(bookKafkaTemplateWithRetry);
    }

    public void send(KafkaTemplate kafkaTemplate) {
        while(true) {
            final long currentMillis = System.currentTimeMillis();
            final String key = BOOK_KEY_PREFIX + currentMillis;

            final Book book = new Book();
            book.setName(BOOK_NAME_PREFIX + currentMillis);

//            final ListenableFuture<SendResult<String, Book>> future = bookKafkaTemplate.sendDefault(key, book);
            final ListenableFuture<SendResult<String, Book>> future = bookKafkaTemplate.sendDefault(key, book);

            System.out.println("Sending message=[" + book + "]");
            future.addCallback(new ListenableFutureCallback<>() {
                public void onSuccess(SendResult<String, Book> result) {
                    System.out.println("After sending, onSuccess, message=[" + result.getProducerRecord().value() +
                            "] with result=[" + result.getRecordMetadata().topic() + ", " +
                            result.getRecordMetadata().offset() + "," + result.getRecordMetadata().partition() +
                            ", " + result.getRecordMetadata().timestamp() + "]");
                }

                public void onFailure(Throwable ex) {
                    System.out.println("After sending, onFailure, unable to send message=[" + book + "] due to : " + ex.getMessage());
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
