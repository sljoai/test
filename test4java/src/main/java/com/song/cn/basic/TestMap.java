package com.song.cn.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestMap {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String>();

        map.put("key", "value");
        map.put("key1", "values");

        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        System.out.println(iterator);
        map.remove("key");
        //注释掉下面两行代码，会跑错
//		iterator = map.entrySet().iterator();
//		System.out.println(iterator);
        if (iterator.hasNext()) {
            System.out.println(iterator.next().getKey());
        }
    }
}
