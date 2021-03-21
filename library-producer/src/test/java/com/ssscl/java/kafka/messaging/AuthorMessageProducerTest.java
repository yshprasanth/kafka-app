package com.ssscl.java.kafka.messaging;

import com.ssscl.kafka.common.Author;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.network.Send;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorMessageProducerTest {

    @Mock
    private KafkaTemplate<String, Author> kafkaTemplate;

    private AuthorMessageProducer authorMessageProducer;

    @Before
    public void setUp() {
        authorMessageProducer = new AuthorMessageProducer();
        ReflectionTestUtils.setField(authorMessageProducer, "authorKafkaTemplate", kafkaTemplate);
    }

    @Test
    public void test_kafka_publish_when_timeout_exception() {
        SendResult<String, Author> mockResult = mock(SendResult.class);
        ListenableFuture<SendResult<String, Author>> listenableFuture = mock(ListenableFuture.class);

        when(kafkaTemplate.sendDefault(anyString(), any()))
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenReturn(listenableFuture);

        // invoke
        authorMessageProducer.sendWithRetryConfig();

        // verify
        verify(kafkaTemplate, times(3)).sendDefault(anyString(), any());
    }

    @Test
    public void test_kafka_publish_when_timeout_exception_more_then_3_times() {
        SendResult<String, Author> mockResult = mock(SendResult.class);
        ListenableFuture<SendResult<String, Author>> listenableFuture = mock(ListenableFuture.class);

        when(kafkaTemplate.sendDefault(anyString(), any()))
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenReturn(listenableFuture);

        // invoke
        authorMessageProducer.sendWithRetryConfig();

        // verify, sendDefault() is invoked only 3 times because of retry config maxAttempts set to 3
        verify(kafkaTemplate, times(3)).sendDefault(anyString(), any());
    }

    @Test
    public void test_kafka_publish_when_authorization_exception() {
        SendResult<String, Author> mockResult = mock(SendResult.class);
        ListenableFuture<SendResult<String, Author>> listenableFuture = mock(ListenableFuture.class);

        when(kafkaTemplate.sendDefault(anyString(), any()))
                .thenThrow(AuthorizationException.class)
                .thenThrow(AuthorizationException.class)
                .thenReturn(listenableFuture);

        // invoke
        authorMessageProducer.sendWithRetryConfig();

        // verify
        verify(kafkaTemplate, times(3)).sendDefault(anyString(), any());
    }

    @Test
    public void test_kafka_publish_when_authorization_exception_more_then_3_times() {
        SendResult<String, Author> mockResult = mock(SendResult.class);
        ListenableFuture<SendResult<String, Author>> listenableFuture = mock(ListenableFuture.class);

        when(kafkaTemplate.sendDefault(anyString(), any()))
                .thenThrow(AuthorizationException.class)
                .thenThrow(AuthorizationException.class)
                .thenThrow(AuthorizationException.class)
                .thenThrow(AuthorizationException.class)
                .thenReturn(listenableFuture);

        // invoke
        authorMessageProducer.sendWithRetryConfig();

        // verify
        verify(kafkaTemplate, times(3)).sendDefault(anyString(), any());
    }

    @Test
    public void test_kafka_publish_when_timeout_and_authorization_exception() throws InterruptedException, ExecutionException, java.util.concurrent.TimeoutException {
        SendResult<String, Author> mockResult = mock(SendResult.class);
        ListenableFuture<SendResult<String, Author>> listenableFuture = mock(ListenableFuture.class);
        when(listenableFuture.get(anyLong(), any())).thenReturn(mockResult);

        when(kafkaTemplate.sendDefault(anyString(), any()))
                .thenThrow(TimeoutException.class)
                .thenThrow(AuthorizationException.class)
                .thenReturn(listenableFuture);

        // invoke
        authorMessageProducer.sendWithRetryConfig();

        // verify
        verify(kafkaTemplate, times(3)).sendDefault(anyString(), any());
    }


}
