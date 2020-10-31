package com.ssscl.java.kafka;


import com.ssscl.java.kafka.messaging.MessageProducer;
import com.ssscl.kafka.common.Book;
import com.ssscl.kafka.common.Library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
@EntityScan(basePackages = {"com.ssscl.kotlin.kafka.config", "java.lang"})
@ComponentScan(basePackages = {"com.ssscl.kotlin.kafka.config", "java.lang"})
public class LibraryProducerApplication {

    @Value(value = "${library.kafka.books.topic.name}")
    private String bookTopicName;

    @Value(value = "${library.kafka.library.topic.name}")
    private String libraryTopicName;

    @Autowired
    @Qualifier("bookKafkaTemplate1")
    private KafkaTemplate<String, Book> bookKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Library> libraryKafkaTemplate;

    public static void main(String[] args) throws Exception {
        System.out.println("inside main... ");
        ConfigurableApplicationContext context = SpringApplication.run(LibraryProducerApplication.class, args);

        MessageProducer<Book> bookMessageProducer = (MessageProducer)context.getBean("bookMessageProducer");

        System.out.println("bookMessageProducer bean: " + bookMessageProducer);
        bookMessageProducer.sendMessage(new Book("MyBook"));
        System.out.println("after sendMessage");

        context.close();
    }

    @Bean(name="bookMessageProducer")
    public MessageProducer<Book> bookProducer() {
        System.out.println("inside bookProducer.." + bookKafkaTemplate);
        bookKafkaTemplate.setDefaultTopic(bookTopicName);
        System.out.println("inside bookProducer, topic:" + bookTopicName);
        return new MessageProducer<Book>(bookKafkaTemplate);
    }

//    @Bean(name="libraryMessageProducer")
//    public MessageProducer<Library> libraryProducer() {
//        libraryKafkaTemplate.setDefaultTopic(libraryTopicName);
//        return new MessageProducer<Library>(libraryKafkaTemplate);
//    }
}