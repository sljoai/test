package com.song.cn.agent.hanlp.ch01;

import com.hankcs.hanlp.HanLP;

public class HelloWorld {

    public static void main(String[] args) {
        HanLP.Config.enableDebug();
        System.out.println(HanLP.segment("王国维和服务员"));
    }
}
