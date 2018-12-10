package com.song.cn.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Main {

    private final Logger LOG = LoggerFactory.getLogger(Main.class);

    static {
        PropertyConfigurator.configure("conf/log4j.properties");
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.80.235:9092,192.168.80.235:9093,192.168.80.235:9094"); // broker list
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        for(int i = 0; i < 10; i++) {
            System.out.println(i);
            // send record to topic 'test'
            producer.send(new ProducerRecord<String, String>("test", "key"+ Integer.toString(i), "value" + Integer.toString(i)));
        }

        producer.close();
    }
}

