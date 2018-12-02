package com.song.cn.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

public class ProtostuffTest {

    /**
     * 产生一个随机的字符串
     */
    public static String randomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int strlen = str.length();
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(strlen);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    private static ResourceObj getObj(String name, String path, int contentSize) {
        ResourceObj obj = new ResourceObj(name, path, "");
        obj.setContent(randomString(contentSize));
        return obj;
    }

    private static long speedTest(int contentSize, int times) {
        ResourceObj obj = getObj("lb.conf", "D:\\Desktop\\redis", contentSize);
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            byte[] bytes = ProtostuffUtil.serializer(obj);
            ProtostuffUtil.deserializer(bytes, ResourceObj.class);
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    private static long speedTestOrg(int contentSize, int times) throws IOException,
            ClassNotFoundException {
        ResourceObj obj = getObj("lb.conf", "D:\\Desktop\\redis", contentSize);
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] bytes = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            ois.readObject();
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println(speedTestOrg(9999999, 1000));
        System.out.println(speedTest(9999999, 1000));
        test();
    }

    private static void test() {
        ResourceObj obj = getObj("lb.conf", "D:\\Desktop\\redis", 88888);
        byte[] bytes = ProtostuffUtil.serializer(obj);

        ResourceObj obj2 = ProtostuffUtil.deserializer(bytes, ResourceObj.class);
        System.out.println(obj2.getName());
        System.out.println(obj2.getPath());
        System.out.println(StringUtils.equals(obj.getContent(), obj2.getContent()));
    }

}
