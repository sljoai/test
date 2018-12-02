package com.song.cn.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 参考：
 * java序列化/反序列化之xstream、protobuf、protostuff 的比较与使用例子
 * https://www.cnblogs.com/xiaoMzjm/p/4555209.html
 */
public class Test4Protostuff {

    private static final int THREAD_NUM = 2;
    /**
     * 存储位数
     */
    public static Map<String, BitSet> bitsets = new ConcurrentHashMap<>();
    /**
     * 记录扫描到的文件
     */
    public static volatile List<File> files = new ArrayList<>();
    /**
     * 记录已处理的文件个数
     */
    public static volatile Integer num = new Integer(0);

    public static void main(String[] args) {
        List<IdnoAndName> ians = new ArrayList<>();
        String path = "D:\\Desktop\\redis";
        Collection<File> fileCollection = FileUtils.listFiles(new File(path), new String[]{"bjson"}, false);

        for (File file : fileCollection) {
            files.add(file);
        }

        ScheduledExecutorService service = Executors.newScheduledThreadPool(THREAD_NUM);

        for (int i = 0; i < THREAD_NUM; i++) {
            Task task = new Task();
            service.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
        }

    }


    /**
     * 将BitSet对象转化为ByteArray
     *
     * @param bitSet
     * @return
     */
    public static byte[] bitSet2ByteArray(BitSet bitSet) {
        byte[] bytes = new byte[bitSet.size() / 8];
        for (int i = 0; i < bitSet.size(); i++) {
            int index = i / 8;
            int offset = 7 - i % 8;
            bytes[index] |= (bitSet.get(i) ? 1 : 0) << offset;
        }
        return bytes;
    }

    /**
     * 将ByteArray对象转化为BitSet
     */
    public static void byteArray2BitSet(BitSet bitSet, byte[] bytes) {
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 7; j >= 0; j--) {
                bitSet.set(index++, (bytes[i] & (1 << j)) >> j == 1);
            }
        }
    }

    public static List<byte[]> serializeProtoStuffProductsList(List<IdnoAndName> ianList) {
        if (ianList == null || ianList.size() <= 0) {
            return null;
        }
        long start = System.currentTimeMillis();
        List<byte[]> bytes = new ArrayList<byte[]>();
        //Schema<IdnoAndName> schema = RuntimeSchema.getSchema(IdnoAndName.class);
        Schema<IdnoAndName> schema = RuntimeSchema.createFrom(IdnoAndName.class);
        LinkedBuffer buffer = LinkedBuffer.allocate(4096);
        byte[] protostuff = null;
        for (IdnoAndName p : ianList) {
            try {
                protostuff = ProtostuffIOUtil.toByteArray(p, schema, buffer);
                bytes.add(protostuff);
            } finally {
                buffer.clear();
            }
        }
        long end = System.currentTimeMillis();
        long userTime = end - start;
        return bytes;
    }

    public static List<IdnoAndName> deserializeProtoStuffDataListToProductsList(List<byte[]> bytesList) {
        if (bytesList == null || bytesList.size() <= 0) {
            return null;
        }
        long start = System.currentTimeMillis();
        Schema<IdnoAndName> schema = RuntimeSchema.getSchema(IdnoAndName.class);
        List<IdnoAndName> list = new ArrayList<>();
        for (byte[] bs : bytesList) {
            IdnoAndName idnoAndName = new IdnoAndName();
            ProtostuffIOUtil.mergeFrom(bs, idnoAndName, schema);
            list.add(idnoAndName);
        }
        long end = System.currentTimeMillis();
        long userTime = end - start;
        return list;
    }
}

class Task implements Runnable {
    @Override
    public void run() {
        File file;
        synchronized (Test4Protostuff.files) {
            if (Test4Protostuff.num > Test4Protostuff.files.size()) {
                return;
            } else {
                file = Test4Protostuff.files.get(Test4Protostuff.num++);
            }
        }
        List<IdnoAndName> ianList = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        //读取文件中的内容，并构造成对象
        LineIterator iterator = null;
        try {
            iterator = FileUtils.lineIterator(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (iterator.hasNext()) {
            String line = iterator.nextLine();
            String[] splits = line.split("\t");
            IdnoAndName ian = new IdnoAndName();
            ian.setIdno(splits[0]);
            ian.setName(splits[1]);
            ianList.add(ian);
            //取前两个字母作为key
            keys.add(splits[0].substring(0, 2));
        }

        //使用protostuff序列化数据
        List<byte[]> bytes = Test4Protostuff.serializeProtoStuffProductsList(ianList);
        for (int i = 0; i < bytes.size(); i++) {
            byte[] byte1 = bytes.get(i);
            String key = keys.get(i);
            BitSet bitSet;
            if (Test4Protostuff.bitsets.containsKey(key)) {
                bitSet = Test4Protostuff.bitsets.get(key);
            } else {
                bitSet = new BitSet();
                Test4Protostuff.bitsets.put(key, bitSet);
            }
            Test4Protostuff.byteArray2BitSet(bitSet, byte1);
        }

        //将序列化的数据写到文件中
        for (int i = 0; i < bytes.size(); i++) {
            try {
                FileUtils.writeByteArrayToFile(new File(file.getParent() + File.separator + "convert.nb"), bytes.get(i), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //FileUtils.writeStringToFile(new File(file.getParent() + File.separator + "convert.nb"), new String(bytes.get(i)), "utf-8", true);
            //FileUtils.write(new File(file.getParent()+File.separator+"convert.nb"),new String(bytes.get(i)),"utf-8",true);
            //FileUtils.write(new File(file.getParent()+File.separator+"convert.nb"),Arrays.toString(bytes.get(i)),"utf-8",true);
        }

        //从文件中读取序列化数据
        LineIterator convertIterator = null;
        try {
            convertIterator = FileUtils.lineIterator(new File(file.getParent() + File.separator + "convert.nb"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<byte[]> result = new ArrayList<>();
        while (convertIterator.hasNext()) {
            String tmp = convertIterator.nextLine();
            if ("".equals(tmp)) {
                continue;
            }
            tmp = "\n" + tmp;
            byte[] bytes1 = tmp.getBytes(StandardCharsets.UTF_8);
            result.add(bytes1);
        }

        //反序列化输出
        List<IdnoAndName> idnoAndNames = Test4Protostuff.deserializeProtoStuffDataListToProductsList(result);
        for (IdnoAndName idnoAndName : idnoAndNames) {
            System.out.println(idnoAndName);
        }
    }
}
