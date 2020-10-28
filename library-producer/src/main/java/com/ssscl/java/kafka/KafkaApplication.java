package com.ssscl.java.kafka;


import com.ssscl.java.kafka.messaging.MessageProducer;
import com.ssscl.kafka.common.Book;
import com.ssscl.kafka.common.Library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.Resource;

@SpringBootApplication
public class KafkaApplication {

    @Value(value = "${book.topic.name}")
    private String bookTopicName;

    @Value(value = "${library.topic.name}")
    private String libraryTopicName;

    @Autowired
    private KafkaTemplate<String, Book> bookKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Library> libraryKafkaTemplate;

    public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext context = SpringApplication.run(KafkaApplication.class, args);

        MessageProducer<Book> bookMessageProducer = context.getBean(MessageProducer.class);
        bookMessageProducer.sendMessage(new Book("MyBook"));

//        MessageProducer<Library> libraryMessageProducer = context.getBean(MessageProducer.class);
//        libraryMessageProducer.sendMessage(new Library("MyLibrary"));

        context.close();
    }

    @Bean(name="bookMessageProducer")
    public MessageProducer<Book> bookProducer() {
        bookKafkaTemplate.setDefaultTopic(bookTopicName);
        return new MessageProducer<Book>(bookKafkaTemplate);
    }

//    @Bean(name="libraryMessageProducer")
//    public MessageProducer<Library> libraryProducer() {
//        libraryKafkaTemplate.setDefaultTopic(libraryTopicName);
//        return new MessageProducer<Library>(libraryKafkaTemplate);
//    }
}