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
 * ��������
 * ������: 0
������: 1
������: 2
������: 3
������: 4
������: 5
������: 6
������: 7
������: 8
������: 9
������: 10
������: 11
������: 12
������: 13
������: 14
������: 15
������: 16
������: 17
������: 18
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
			System.out.println("������: " + i);
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
		properties.put("zookeeper.connect", "192.168.80.235:2181,192.168.80.235:2182,192.168.80.235:2183");//����zk
		properties.put("serializer.class", StringEncoder.class.getName());
		properties.put("metadata.broker.list", "192.168.80.235:9092,192.168.80.235:9093,192.168.80.235:9094");// ����kafka broker
		return new Producer<Integer, String>(new ProducerConfig(properties));
	 }


	public static void main(String[] args) {
		new KafkaProducer("test").start();// ʹ��kafka��Ⱥ�д����õ����� test

	}
	 
}
