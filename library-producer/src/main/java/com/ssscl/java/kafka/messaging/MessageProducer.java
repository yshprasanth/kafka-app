package com.ssscl.java.kafka.messaging;

import com.ssscl.kafka.common.Book;
import com.ssscl.kafka.common.Library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class MessageProducer<T> {

    private KafkaTemplate<String, T> kafkaTemplate;

    public MessageProducer(final KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(final T message) {

        final ListenableFuture<SendResult<String, T>> future = kafkaTemplate.send(kafkaTemplate.getDefaultTopic(), message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, T>>() {

            public void onSuccess(SendResult<String, T> result) {
                System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata()
                        .offset() + "]");
            }

            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
            }
        });
    }


}
