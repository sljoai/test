package com.song.cn.agent.kafka.controller;

import org.apache.kafka.clients.admin.AdminClient;

import java.util.Properties;

public class AdminClientTest {

    public static void main(String[] args) {
        String brokerUrl = "172.16.117.5:9092";
        String topic = "auto_title";
        Properties properties = new Properties();
        AdminClient client = AdminClient.create(properties);


    }
}
