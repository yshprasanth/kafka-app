package com.ssscl.java.kafka;


import com.ssscl.java.kafka.component.CommandLineComponent;
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

        final BookMessageProducer bookMessageProducer = (BookMessageProducer)context.getBean("bookMessageProducer");
        bookMessageProducer.start();

        final CommandLineComponent commandLineComponent = (CommandLineComponent) context.getBean("commandLineComponent");
        commandLineComponent.start();
    }
}