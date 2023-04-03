package com.sipankaj.kafka;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileInputStream;
import java.util.Properties;

public class MyProducer {

    public static void main(String[] args) throws Exception {

        // Load the configuration properties
        Properties properties = new Properties();
        properties.load(new FileInputStream("/etc/kafka/secrets/producer.properties"));

        // Create the Kafka producer
        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(properties);

        // Send a message to the Kafka topic
        ProducerRecord<String, String> record = new ProducerRecord<>("test-topic",null, "Hello, Kafka mTLS!");
        producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    System.out.println("Message sent successfully to Kafka topic " + metadata.topic() +
                            " at partition " + metadata.partition() + " and offset " + metadata.offset());
                } else {
                    System.out.println("Error sending message to Kafka: " + exception.getMessage());
                    exception.getStackTrace();
                }
            }
        });
        System.out.println("Sent Message");
        // Flush and close the producer
        producer.flush();
        producer.close();
    }
}

