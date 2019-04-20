package com.song.cn.kafka.comsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaConsumer1 {

    private final Logger LOG = LoggerFactory.getLogger(KafkaConsumer1.class);

    static {
        PropertyConfigurator.configure("conf/log4j.properties");
    }

    public static void main(String[] args) {
        // write your code here
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.80.235:9092"); // broker list
//        props.put("zookeeper.connect", "192.168.80.235:2181,192.168.80.235:2182,192.168.80.235:2183");//声明zk
        props.put("group.id", "testGroup");
        props.put("enable.auto.commit", "false");
//        props.put("auto.commit.interval.ms", "1000");
        props.put("max.poll.records",10);
        props.put("max.poll.interval.ms",30000);
        props.put("auto.offset.reset","earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        String topic = "test";
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(topic)); // subscribe "test" topic

//        TopicPartition topicPartition1 = new TopicPartition(topic,0);
//        TopicPartition topicPartition2 = new TopicPartition(topic,1);
//        TopicPartition topicPartition3 = new TopicPartition(topic,2);
//        //分配topic和partition
//        consumer.assign(Arrays.asList(topicPartition1,topicPartition2,topicPartition2));
//        //指定从这个topic和partition的开始位置获取
//        consumer.seekToBeginning(Arrays.asList(topicPartition1));//不改变当前offset
        //从指定topic的分区及偏移量开始读取消息
//        consumer.seek(topicPartition1,0);//不改变当前offset
        long start = 0;
        long end = 1000;
        long num = end-start+1;
        long total = 0;
        int i=0;
        while (true) {
            if(total>num){
                break;
            }
            //取消息数据
            ConsumerRecords<String, String> records = consumer.poll(1000);
            records.count();
            System.out.println("第 "+i+" 次");
//            for (ConsumerRecord<String, String> record : records)
//                System.out.printf("partion = %d, offset = %d, key = %s, value = %s%n", record.partition(),record.offset(), record.key(), record.value());
            //数据处理
            for(TopicPartition partition:records.partitions()){
                List<ConsumerRecord<String,String>> partitionRecords = records.records(partition);
                for (ConsumerRecord<String, String> record : partitionRecords) {
                    System.out.printf("partion = %d, offset = %d, key = %s, value = %s%n", record.partition(),record.offset(), record.key(), record.value());
                }
                //取得当前分区下读取的最后一条记录的offset
                long lastOffset = partitionRecords.get(partitionRecords.size()-1).offset();
                //提交offset
                consumer.commitSync(Collections.singletonMap(partition,new OffsetAndMetadata(lastOffset+1)));
                total += partitionRecords.size();
                consumer.position()
            }
            //total+=records.count();
            i++;
        }
        consumer.close();
    }
}

