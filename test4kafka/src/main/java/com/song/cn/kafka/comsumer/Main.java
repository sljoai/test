package com.song.cn.kafka.comsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Arrays;
import java.util.Properties;

public class Main {

    private final Logger LOG = LoggerFactory.getLogger(Main.class);

    static {
        PropertyConfigurator.configure("conf/log4j.properties");
    }

    public static void main(String[] args) {
        // write your code here
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.80.235:9092,192.168.80.235:9093,192.168.80.235:9094"); // broker list
        props.put("group.id", "testGroup");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList("test")); // subscribe "test" topic
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }

    }
}

