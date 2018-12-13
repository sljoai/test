package com.song.cn.kafka.comsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
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
        //consumer.subscribe(Arrays.asList("test")); // subscribe "test" topic
        //分配topic和partition
        //consumer.assign(Arrays.asList(new TopicPartition("test",0)));
        //指定从这个topic和partition的开始位置获取
        consumer.seekToBeginning(Arrays.asList(new TopicPartition("test", 0)));//不改变当前offset
        //从指定topic的分区及偏移量开始读取消息
        consumer.seek(new TopicPartition("test",0),10);//不改变当前offset
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }

    }
}

