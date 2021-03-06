package com.song.cn.agent.kafka.producer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaProducer {

    private final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    static {
        PropertyConfigurator.configure("conf/log4j.properties");
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.80.235:9092,192.168.80.235:9093,192.168.80.235:9094"); // broker list
        props.put("acks", "1");
        props.put("retries", 0);
//        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer(props);

        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            // send record to topic 'test'
            Future<RecordMetadata> record = producer.send(new ProducerRecord<String, String>("test", "key" + Integer.toString(i), "value" + Integer.toString(i)));
            try {
                RecordMetadata metadata = record.get();
                long offset = metadata.offset();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        producer.close();
    }
}

