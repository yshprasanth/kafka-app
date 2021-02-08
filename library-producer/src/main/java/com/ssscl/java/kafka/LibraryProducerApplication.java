package com.ssscl.java.kafka;


import com.ssscl.java.kafka.component.AuthorCommandLineComponent;
import com.ssscl.java.kafka.component.BooksCommandLineComponent;
import com.ssscl.java.kafka.messaging.AuthorMessageProducer;
import com.ssscl.java.kafka.messaging.BookMessageProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ssscl.kotlin.kafka", "com.ssscl.java.kafka"})
public class LibraryProducerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LibraryProducerApplication.class, args);

//        final BookMessageProducer bookMessageProducer = (BookMessageProducer)context.getBean("bookMessageProducer");
//        bookMessageProducer.start();
//
//        final BooksCommandLineComponent booksCommandLineComponent = (BooksCommandLineComponent) context.getBean("booksCommandLineComponent");
//        booksCommandLineComponent.start();

        final AuthorMessageProducer authorMessageProducer = (AuthorMessageProducer)context.getBean("authorMessageProducer");
        authorMessageProducer.start();

        final AuthorCommandLineComponent authorCommandLineComponent = (AuthorCommandLineComponent) context.getBean("authorCommandLineComponent");
        authorCommandLineComponent.start();
    }
}