package com.song.cn.kafka.producer;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.song.cn.kafka.comsumer.KafkaConsumer;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 发送数据
 * 发送了: 0
发送了: 1
发送了: 2
发送了: 3
发送了: 4
发送了: 5
发送了: 6
发送了: 7
发送了: 8
发送了: 9
发送了: 10
发送了: 11
发送了: 12
发送了: 13
发送了: 14
发送了: 15
发送了: 16
发送了: 17
发送了: 18
 * @author zm
 *
 */
public class KafkaProducer extends Thread{

    private final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    static {
        PropertyConfigurator.configure("conf/log4j.properties");
    }

	private String topic;

	public KafkaProducer(String topic){
		super();
		this.topic = topic;
	}


	@Override
	public void run() {
		Producer producer = createProducer();
		int i=0;
		while(true){
			producer.send(new KeyedMessage<Integer, String>(topic, "message: " + i));
			System.out.println("发送了: " + i);
			try {
				TimeUnit.SECONDS.sleep(1);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Producer createProducer() {
		Properties properties = new Properties();
		properties.put("zookeeper.connect", "192.168.80.235:2181,192.168.80.235:2182,192.168.80.235:2183");//声明zk
		properties.put("serializer.class", StringEncoder.class.getName());
		properties.put("metadata.broker.list", "192.168.80.235:9092,192.168.80.235:9093,192.168.80.235:9094");// 声明kafka broker
		return new Producer<Integer, String>(new ProducerConfig(properties));
	 }


	public static void main(String[] args) {
		new KafkaProducer("test").start();// 使用kafka集群中创建好的主题 test

	}
	 
}
