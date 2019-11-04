package com.song.cn.basic.designpattern.singleton;

/**
 * 基于静态内部类实现单例
 */
public class HolderSingleton {
    private static class HolderInstance {
        private static final HolderInstance instance = new HolderInstance();
    }

    public static final HolderInstance getInstance() {
        return HolderInstance.instance;
    }
}
