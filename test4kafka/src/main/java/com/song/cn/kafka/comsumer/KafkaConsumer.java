package com.song.cn.kafka.comsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 接收数据
 * 接收到: message: 10
接收到: message: 11
接收到: message: 12
接收到: message: 13
接收到: message: 14
 * @author zm
 *
 */
public class KafkaConsumer extends Thread{

	private final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

	static {
		PropertyConfigurator.configure("conf/log4j.properties");
	}

	private String topic;
	
	public KafkaConsumer(String topic){
		super();
		this.topic = topic;
	}
	
	
	@Override
	public void run() {
		ConsumerConnector consumer = createConsumer();
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, 1); // 一次从主题中获取一个数据
		 Map<String, List<KafkaStream<byte[], byte[]>>>  messageStreams = consumer.createMessageStreams(topicCountMap);
		 KafkaStream<byte[], byte[]> stream = messageStreams.get(topic).get(0);// 获取每次接收到的这个数据
		 ConsumerIterator<byte[], byte[]> iterator =  stream.iterator();
		 while(iterator.hasNext()){
			 String message = new String(iterator.next().message());
			 System.out.println("接收到: " + message);
		 }
	}

	private ConsumerConnector createConsumer() {
		Properties properties = new Properties();
		properties.put("zookeeper.connect", "192.168.80.235:2181,192.168.80.235:2182,192.168.80.235:2183");//声明zk
		properties.put("group.id", "group1");
		return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
	 }
	
	
	public static void main(String[] args) {
		new KafkaConsumer("test").start();// 使用kafka集群中创建好的主题 test
		
	}
	 
}
