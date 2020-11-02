package com.ssscl.java.kafka.component;

import com.ssscl.java.kafka.messaging.BookMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CommandLineComponent implements Runnable {

    private static final String EXIT_COMMAND = "exit";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private BookMessageProducer bookMessageProducer;

    public void start() {
        executorService.submit(this);
    }

    @Override
    public void run() {
        final Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()) {
            final String command = scanner.nextLine();
            if(EXIT_COMMAND.equalsIgnoreCase(command)) {
                bookMessageProducer.terminate();
                System.exit(1);
            }
        }
    }
}
