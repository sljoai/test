package com.song.cn.basic.other;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {
    /**
     * 截取时间字符串
     * 日期字符串转化为日期再转化为日期，消除日期字符串的秒
     * 将更具体的时间进行转化为模糊时间
     *
     * @param date  String 时间格式的字符串“yyyy-MM-dd HH-mm-ss”
     * @param geshi 截取的时间格式"YYYY-MM-dd HH:mm"
     * @return 格式为"YYYY-MM-dd HH:mm"的时间字符串
     * @see String s=DataFormat.toTimestamp("2017-03-23 10:57:57","yyyy-MM-dd");
     */
    public static String toTimestamp(String date, String geshi) {
        SimpleDateFormat df = new SimpleDateFormat(geshi);
        Date s = null;
        String result = null;
        try {
            s = df.parse(date);
            result = df.format(s);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    //传入时间
    public static String toTimestamp(Date date, String geshi) {
        SimpleDateFormat df = new SimpleDateFormat(geshi);
        Date s = date;
        String result = null;
        result = df.format(s);
        return result;
    }

    public static void main(String[] args) {
        String s = DateTest.toTimestamp("20170323", "yyyyMMdd");
        System.out.print(s);
    }
}
