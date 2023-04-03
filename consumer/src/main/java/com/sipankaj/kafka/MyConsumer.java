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

    public static void main(String[] args) throws Exception {

        // Load the configuration properties
        Properties properties = new Properties();
        properties.load(new FileInputStream("/etc/kafka/secrets/consumer.properties"));
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        // Create the Kafka consumer
        Consumer<String, String> consumer = new KafkaConsumer<>(properties);

        // Subscribe to the Kafka topic
        consumer.subscribe(Collections.singletonList("test-topic"));
        // Consume messages from the Kafka topic
        while (true) {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                records.forEach(record -> System.out.printf("Topic: %s, Partition: %d, Offset: %d, Key: %s, Value: %s\n",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value()));
                consumer.commitAsync();
        }

    }
}

