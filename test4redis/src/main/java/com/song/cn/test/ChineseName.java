package com.song.cn.test;

import java.io.UnsupportedEncodingException;

public class ChineseName {

    public static void main(String[] args) {
        ChineseName d = new ChineseName();
        System.out.println(d.getName());//输出随机的中文名字
    }

    //获得汉字名字
    public static String getName() {
        String name = "";
        int chineseNameNum = (int) (Math.random() * 3 + 2);
        for (int i = 1; i <= chineseNameNum; i++) {
            name += getChinese();
        }
        return name;
    }

    //获得单个汉字
    public static String getChinese() {
        String chinese = "";
        int i = (int) (Math.random() * 40 + 16);
        int j = (int) (Math.random() * 94 + 1);
        if (i == 55) {
            j = (int) (Math.random() * 89 + 1);
        }
        byte[] bytes = {(byte) (i + 160), (byte) (j + 160)};
        try {
            chinese = new String(bytes, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return chinese;
    }
}