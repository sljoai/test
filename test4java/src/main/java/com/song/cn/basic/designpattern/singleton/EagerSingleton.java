package com.song.cn.basic.designpattern.singleton;

/**
 * 饿汉单例
 */
public class EagerSingleton {

    private static EagerSingleton instance = new EagerSingleton();

    public static EagerSingleton getInstance() {
        return instance;
    }

}
