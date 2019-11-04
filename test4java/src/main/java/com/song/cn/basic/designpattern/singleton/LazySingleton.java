package com.song.cn.basic.designpattern.singleton;

/**
 * 懒汉单例
 */
public class LazySingleton {
    private static LazySingleton instance = null;

    public static synchronized LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }

        return instance;
    }
}
