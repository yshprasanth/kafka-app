package com.ssscl.java.kafka.messaging;

import com.ssscl.kafka.common.Author;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
@DependsOn("authorKafkaTemplate")
public class AuthorMessageProducer implements Runnable {

    private static final long THREAD_WAIT_TIME_MILLS = 2000l;

    @Autowired
    private KafkaTemplate<String, Author> authorKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Author> authorKafkaTemplateWithRetry;

    @Value("${library.kafka.retry.maxAttempts}")
    private int retryMaxAttempts = 3;

    @Value("${library.kafka.retry.waitDuration}")
    private long retryWaitDuration = 10;

    @Value("${library.kafka.retry.resultWaitDuration}")
    private long resultWaitDuration = 60;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final static String AUTHOR_NAME_PREFIX = "Mr.";
    private final static String AUTHOR_KEY_PREFIX = "Key_";

    private final Retry retry;

    public AuthorMessageProducer() {
        RetryConfig config = RetryConfig.custom().
                maxAttempts(retryMaxAttempts).
                waitDuration(Duration.of(retryWaitDuration, SECONDS)).
                retryExceptions(TimeoutException.class, AuthorizationException.class).
                build();
        RetryRegistry registry = RetryRegistry.of(config);
        retry = registry.retry("authorKafkaTemplate", config);
    }

    public void start() {
        executorService.submit(this);
    }

    @Override
    public void run() {
        while(true) {
            sendWithRetryConfig();
        }
    }

    public void sendWithRetryConfig() {
        Callable<ListenableFuture<SendResult<String, Author>>> kafkaPublisherCallable = Retry.decorateCallable(retry, () -> {
            System.out.println("trying to invoke send again");
            return send(authorKafkaTemplate, true, 0);
        });

        try {
            ListenableFuture<SendResult<String, Author>> listenableFuture = kafkaPublisherCallable.call();
            listenableFuture.get(resultWaitDuration, TimeUnit.SECONDS);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public ListenableFuture<SendResult<String, Author>> sendWithRetry() {
        return send(authorKafkaTemplateWithRetry, true, 0);
    }

    public ListenableFuture<SendResult<String, Author>> sendWithoutRetry() {
        return send(authorKafkaTemplate, false, 0);
    }

    private ListenableFuture<SendResult<String, Author>> send(KafkaTemplate kafkaTemplate, boolean isRetry, int retryIndex) {
        final long currentMillis = System.currentTimeMillis();
        final String key = AUTHOR_KEY_PREFIX + currentMillis;

        final Author author = new Author();
        author.setFName(AUTHOR_NAME_PREFIX + currentMillis);

        ListenableFuture<SendResult<String, Author>> listenableFuture =
                kafkaPublish(kafkaTemplate, key, author, isRetry, retryIndex);

        try {
            Thread.sleep(THREAD_WAIT_TIME_MILLS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return listenableFuture;
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

    private ListenableFuture<SendResult<String, Author>> kafkaPublish(KafkaTemplate kafkaTemplate, String key, Author author, boolean isRetry, int retryIndex) {
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
        return future;
    }

    public void terminate() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
