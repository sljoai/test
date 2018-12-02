package com.song.cn.redis;


import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.util.JedisClusterCRC16;

import java.util.*;

public class TestRedis {
    private static JedisCluster jedisCluster;
    private static Jedis jedis = new Jedis("");

    static {
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        nodes.add(new HostAndPort("uplooking01", 7000));
        nodes.add(new HostAndPort("uplooking01", 7001));
        nodes.add(new HostAndPort("uplooking01", 7002));
        nodes.add(new HostAndPort("uplooking01", 7003));
        nodes.add(new HostAndPort("uplooking01", 7004));
        nodes.add(new HostAndPort("uplooking01", 7005));
        jedisCluster = new JedisCluster(nodes);//得到的是redis的集群模式
    }

    /**
     * 提供了Jedis的对象
     *
     * @return
     */
    public static JedisCluster getJedis() {
        return jedisCluster;
    }

    /**
     * 资源释放
     *
     * @param jedis
     */
    public static void returnJedis(JedisCluster jedis) {

    }

    public static void main(String[] args) {
        List<Integer> slots = new ArrayList<>();
        for(int i=0;i<20;i++){
            int slot = JedisClusterCRC16.getSlot(Integer.toString(i))%16384;
            slots.add(slot);
        }
        Collections.sort(slots);

        for(Integer slot:slots){
            System.out.println(slot);
        }
    }

}
