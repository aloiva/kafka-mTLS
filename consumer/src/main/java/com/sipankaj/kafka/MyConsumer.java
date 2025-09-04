package com.sipankaj.kafka;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class MyConsumer {

    private static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(new FileInputStream("/etc/kafka/secrets/consumer.properties"));
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load consumer properties", e);
        }
    }

    public static String receiveFromKafka() {
        Consumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList("test-topic"));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        StringBuilder result = new StringBuilder();
        records.forEach(record -> result.append(String.format("Topic: %s, Partition: %d, Offset: %d, Key: %s, Value: %s\n",
                record.topic(), record.partition(), record.offset(), record.key(), record.value())));
        consumer.commitAsync();
        consumer.close();
        return result.length() > 0 ? result.toString() : "No messages received.";
    }

    public static void main(String[] args) throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(8080), 0);
        server.createContext("/receive", new com.sun.net.httpserver.HttpHandler() {
            @Override
            public void handle(com.sun.net.httpserver.HttpExchange exchange) throws java.io.IOException {
                String response = receiveFromKafka();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                java.io.OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        server.setExecutor(null); // creates a default executor
        System.out.println("HTTP server started on port 8080. Call /receive to get messages from Kafka.");
        server.start();
    }
}

