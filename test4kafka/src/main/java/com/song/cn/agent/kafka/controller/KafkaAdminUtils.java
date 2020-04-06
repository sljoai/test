package com.song.cn.agent.kafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import kafka.admin.ConsumerGroupCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaAdminUtils {

    private static final String ADMIN_KAFKA_CONF_PROPERTIES = "/admin_kafka.properties";
    // private static final String ADMIN_KAFKA_CONF_ENV =
    // System.getenv("UEP_CONF_DIR") + ADMIN_KAFKA_CONF_PROPERTIES;

    private static long admin_kafka_conf_properties_lastmodified;

    private static AdminClient client = null;
    private static Properties p = null;

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new DefaultScalaModule());
    }

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // 新建topic
    public static boolean createTopic(String name, int numPartitions, short replicationFactor,
                                      Map<String, String> configs) {
        AdminClient client = getAdminClient();
        if (client == null) {
            return false;
        }

        Set<String> topics = null;
        try {
            topics = client.listTopics().names().get();
            if (topics.contains(name)) {
                return true;
            }

            NewTopic topic = new NewTopic(name, numPartitions, replicationFactor);
            if (configs != null && !configs.isEmpty()) {
                topic.configs(configs);
            }

            client.createTopics(Lists.newArrayList(topic)).all().get();

            topics = client.listTopics().names().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("创建topic:{}出错1", name, e);
        } catch (ExecutionException e) {
            log.error("创建topic:{}出错2", name, e);
        }

        return topics.contains(name);

    }

    // 删除topic
    public static boolean delTopic(String name) {
        AdminClient client = getAdminClient();
        if (client == null) {
            return false;
        }

        Set<String> topics = null;
        try {
            topics = client.listTopics().names().get();
            if (!topics.contains(name)) {
                return true;
            }

            client.deleteTopics(Lists.newArrayList(name)).all().get();

            topics = client.listTopics().names().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("删除topic:{}出错1", name, e);
        } catch (ExecutionException e) {
            log.error("删除topic:{}出错2", name, e);
        }

        return !topics.contains(name);
    }

    // 增加partitions
    public static boolean increasePartitions(String name, int numPartitions) {
        AdminClient client = getAdminClient();
        if (client == null) {
            return false;
        }

        try {

            // 获取当前分区数量
            int current = getAdminClient().describeTopics(Lists.newArrayList(name)).all().get().get(name).partitions()
                    .size();

            if (numPartitions <= current) {
                return true;
            }

            NewPartitions partitions = NewPartitions.increaseTo(numPartitions);
            client.createPartitions(Collections.singletonMap(name, partitions)).all().get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("修改topic:{}的partitions数量:{}出错1", name, numPartitions, e);
        } catch (ExecutionException e) {
            log.error("修改topic:{}的partitions数量:{}出错2", name, numPartitions, e);
        }

        return true;
    }

    // 获取分组信息 offsets.storage=kafka
    public static String describeKafkaGroup(String group) {
        String rs = null;

        String bootstrapServer = getProperties().getProperty("bootstrap.servers");
        String[] args = {"--describe", "--bootstrap-server", bootstrapServer, "--group", group};

        ConsumerGroupCommand.ConsumerGroupCommandOptions options = new ConsumerGroupCommand.ConsumerGroupCommandOptions(
                args);
        // kafaka 2.x版本已不支持zookeeper存储offsets了
        // ConsumerGroupCommand.ConsumerGroupService service = new ConsumerGroupCommand.KafkaConsumerGroupService(options);
        ConsumerGroupCommand.ConsumerGroupService service = new ConsumerGroupCommand.ConsumerGroupService(options);

        try {
            FutureTask<String> task = new FutureTask<>(
                    () -> mapper.writeValueAsString(service.collectGroupOffsets()._2.get()));
            executorService.execute(task);
            rs = task.get(3, TimeUnit.SECONDS);

            // Future<String> future =
            // executorService.submit(() ->
            // mapper.writeValueAsString(service.collectGroupOffsets()._2.get()));
            //
            // rs = future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取kafka分组信息offsets.storage=kafka出错", e);
        }

        service.close();

        return rs;
    }

    // 加载配置
    private static Properties getProperties() {
        URL url = KafkaAdminUtils.class.getResource(ADMIN_KAFKA_CONF_PROPERTIES);
        if (url == null || StringUtils.isBlank(url.getFile())) {
            throw new IllegalArgumentException(ADMIN_KAFKA_CONF_PROPERTIES + "配置文件找不到！");
        }
        File f = new File(url.getFile());

        if (p != null && f.lastModified() <= admin_kafka_conf_properties_lastmodified) {
            return p;
        }
        Properties properties = new Properties();

        // try (BufferedReader reader = Files.newReader(new File(ADMIN_KAFKA_CONF_ENV),
        // Charsets.UTF_8)) {
        try (BufferedReader reader = Files.newReader(f, Charsets.UTF_8)) {
            properties.load(reader);

            p = properties;
            admin_kafka_conf_properties_lastmodified = f.lastModified();

        } catch (IOException e) {
            log.error("加载admin_kafka.properties配置出错了", e);
        }

        return properties;
    }

    private static AdminClient getAdminClient() {
        Properties p = getProperties();
        if (p == null) {
            return null;
        }

        if (client == null) {
            client = KafkaAdminClient.create(p);
        }

        return client;
    }

}
