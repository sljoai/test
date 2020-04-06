package com.song.cn.agent.test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.BitSet;

public class Test4BitSet {

    /**
     * @param args
     */
    public static void main(String[] args) {
        test4bitset();
        test4hash();
        test4bloomfilter();
    }

    public static void test4BitsetInRedis() {
        BitSet bitSet = new BitSet();

    }

    public static void test4bloomfilter() {
        Charset charset = Charset.forName("utf-8");
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(charset), 2 << 21);//指定bloomFilter的容量
        String url = "www.baidu.com/a";
        bloomFilter.put(url);

        System.out.println(bloomFilter.mightContain(url));
    }

    public static void test4hash() {
        String tmp1 = "19919898\t将";
        String tmp2 = "19919898\t来";
        String[] split1 = tmp1.split("\t");
        String[] split2 = tmp2.split("\t");

        int t1hc1 = split1[0].hashCode() & Integer.MAX_VALUE;
        int t1hc2 = split1[1].hashCode() & Integer.MAX_VALUE;
        int t1hc3 = Integer.toString(t1hc1).concat(Integer.toString(t1hc2)).hashCode();
        int t1hc4 = Integer.toString(t1hc1).concat(split1[1]).hashCode();

        int t2hc1 = split2[0].hashCode() & Integer.MAX_VALUE;
        int t2hc2 = split2[1].hashCode() & Integer.MAX_VALUE;
        int t2hc3 = Integer.toString(t2hc1).concat(Integer.toString(t2hc2)).hashCode();
        int t2hc4 = Integer.toString(t1hc1).concat(split2[1]).hashCode();

        System.out.println(t1hc3);
        System.out.println(t2hc3);

    }

    public static void test4bitset() {
        int[] shu = {2, 42, 5, 6, 6, 18, 33, 15, 25, 31, 28, 37};
        BitSet bm1 = new BitSet(Test4BitSet.getMaxValue(shu));
        System.out.println("bm1.size()--" + bm1.size());

        Test4BitSet.putValueIntoBitSet(shu, bm1);
        printBitSet(bm1);
    }

    //初始全部为false，这个你可以不用，因为默认都是false
    public static void initBitSet(BitSet bs) {
        for (int i = 0; i < bs.size(); i++) {
            bs.set(i, false);
        }
    }

    //打印
    public static void printBitSet(BitSet bs) {
        StringBuffer buf = new StringBuffer();
        buf.append("[\n");
        for (int i = 0; i < bs.size(); i++) {
            if (i < bs.size() - 1) {
                buf.append(Test4BitSet.getBitTo10(bs.get(i)) + ",");
            } else {
                buf.append(Test4BitSet.getBitTo10(bs.get(i)));
            }
            if ((i + 1) % 8 == 0 && i != 0) {
                buf.append("\n");
            }
        }
        buf.append("]");
        System.out.println(buf.toString());
    }

    //找出数据集合最大值
    public static int getMaxValue(int[] zu) {
        int temp = 0;
        temp = zu[0];
        for (int i = 0; i < zu.length; i++) {
            if (temp < zu[i]) {
                temp = zu[i];
            }
        }
        System.out.println("maxvalue:" + temp);
        return temp;
    }

    //放值
    public static void putValueIntoBitSet(int[] shu, BitSet bs) {
        for (int i = 0; i < shu.length; i++) {
            bs.set(shu[i], true);
        }
    }

    //true,false换成1,0为了好看
    public static String getBitTo10(boolean flag) {
        String a = "";
        if (flag == true) {
            return "1";
        } else {
            return "0";
        }
    }
}
