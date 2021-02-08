package com.ssscl.java.kafka.messaging;

import com.ssscl.kafka.common.PublisherOrg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LibraryMessageProducer {

    @Autowired
    private KafkaTemplate<String, PublisherOrg> libraryKafkaTemplate;

    public LibraryMessageProducer() {
    }
}
