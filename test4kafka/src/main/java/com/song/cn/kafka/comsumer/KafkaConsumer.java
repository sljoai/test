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
 * ��������
 * ���յ�: message: 10
���յ�: message: 11
���յ�: message: 12
���յ�: message: 13
���յ�: message: 14
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
		topicCountMap.put(topic, 1); // һ�δ������л�ȡһ������
		 Map<String, List<KafkaStream<byte[], byte[]>>>  messageStreams = consumer.createMessageStreams(topicCountMap);
		 KafkaStream<byte[], byte[]> stream = messageStreams.get(topic).get(0);// ��ȡÿ�ν��յ����������
		 ConsumerIterator<byte[], byte[]> iterator =  stream.iterator();
		 while(iterator.hasNext()){
			 String message = new String(iterator.next().message());
			 System.out.println("���յ�: " + message);
		 }
	}

	private ConsumerConnector createConsumer() {
		Properties properties = new Properties();
		properties.put("zookeeper.connect", "192.168.80.235:2181,192.168.80.235:2182,192.168.80.235:2183");//����zk
		properties.put("group.id", "group1");
		return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
	 }
	
	
	public static void main(String[] args) {
		new KafkaConsumer("test").start();// ʹ��kafka��Ⱥ�д����õ����� test
		
	}
	 
}
