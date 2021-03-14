package com.ssscl.java.kafka.messaging;

import com.ssscl.kafka.common.Author;
import com.ssscl.kafka.common.Book;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private KafkaTemplate<String, Author> authorKafkaTemplateWithRetry;

    @Value("${library.kafka.retries:3}")
    private int retries;

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
        sendWithRetry();
    }

    public void sendWithRetry() {
        send(authorKafkaTemplateWithRetry, true, 0);
    }

    public void sendWithoutRetry() {
        send(authorKafkaTemplate, false, 0);
    }

    public void send(KafkaTemplate kafkaTemplate, boolean isRetry, int retryIndex) {
        while (true) {
            final long currentMillis = System.currentTimeMillis();
            final String key = AUTHOR_KEY_PREFIX + currentMillis;

            final Author author = new Author();
            author.setFName(AUTHOR_NAME_PREFIX + currentMillis);

            kafkaPublish(kafkaTemplate, key, author, isRetry, retryIndex);

            try {
                Thread.sleep(THREAD_WAIT_TIME_MILLS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleFailure(KafkaTemplate kafkaTemplate, String key, Author author, boolean isRetry, int retryIndex,
                              Throwable exception) {
        System.out.println("After sending, onFailure, unable to send message=[" + author + "] due to : " + exception.getMessage());
//        if (retryIndex < retries) {
//            try {
//                Thread.sleep(THREAD_WAIT_TIME_MILLS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Author newAuthor = new Author(author.getFName(), author.getLName());
//            kafkaPublish(kafkaTemplate, key+"_retry"+retryIndex, newAuthor, isRetry, ++retryIndex);
//        }
//        else {
//            System.out.println("****** Failure even after sending " + retryIndex + " times ******");
//        }

    }

    private void kafkaPublish(KafkaTemplate kafkaTemplate, String key, Author author, boolean isRetry, int retryIndex) {
        final ListenableFuture<SendResult<String, Author>> future = kafkaTemplate.sendDefault(key, author);

        System.out.println("Sending message=[" + author + "]" + (isRetry ? " with retry: " + retryIndex : ""));
        future.addCallback(new ListenableFutureCallback<>() {
            public void onSuccess(SendResult<String, Author> result) {
                System.out.println("After sending, onSuccess, message=[" + result.getProducerRecord().value() +
                        "] with result=[" + result.getRecordMetadata().topic() + ", " +
                        result.getRecordMetadata().offset() + "," + result.getRecordMetadata().partition() +
                        ", " + result.getRecordMetadata().timestamp() + "]");
            }

            public void onFailure(Throwable ex) {
                handleFailure(kafkaTemplate, key, author, isRetry, retryIndex, ex);
            }
        });
    }

    public void terminate() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
