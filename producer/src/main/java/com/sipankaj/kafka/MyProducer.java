package com.sipankaj.kafka;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileInputStream;
import java.util.Properties;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class MyProducer {

    private static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(new FileInputStream("/etc/kafka/secrets/producer.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load producer properties", e);
        }
    }

    public static String sendToKafka() {
        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>("test-topic", null, "Hello, Kafka mTLS!");
        final StringBuilder result = new StringBuilder();
        producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    result.append("Message sent successfully to Kafka topic " + metadata.topic() +
                            " at partition " + metadata.partition() + " and offset " + metadata.offset());
                } else {
                    result.append("Error sending message to Kafka: " + exception.getMessage());
                }
            }
        });
        producer.flush();
        producer.close();
        return result.length() > 0 ? result.toString() : "Sent Message";
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(8080), 0);
        server.createContext("/send", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = sendToKafka();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        server.setExecutor(null); // creates a default executor
        System.out.println("HTTP server started on port 8080. Call /send to send message to Kafka.");
        server.start();
    }
}

