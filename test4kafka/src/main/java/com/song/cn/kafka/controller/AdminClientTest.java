package com.song.cn.kafka.controller;

import kafka.admin.AdminClient;
import kafka.coordinator.group.GroupOverview;
import org.apache.kafka.common.TopicPartition;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminClientTest {

    public static void main(String[] args) {
        String brokerUrl = "172.16.117.5:9092";
        String topic = "auto_title";
        AdminClient client = AdminClient.createSimplePlaintext(brokerUrl);
        List<GroupOverview> allGroups = scala.collection.JavaConversions.seqAsJavaList(client.listAllGroupsFlattened().toSeq());
        Set<String> groups = new HashSet<>();
        for (GroupOverview overview : allGroups) {
            String groupID = overview.groupId();
            Map<TopicPartition, Object> offsets = scala.collection.JavaConversions.mapAsJavaMap(client.listGroupOffsets(groupID));
            Set<TopicPartition> partitions = offsets.keySet();
            for (TopicPartition tp : partitions) {
                if (tp.topic().equals(topic)) {
                    groups.add(groupID);
                }
            }
        }
    }
}
