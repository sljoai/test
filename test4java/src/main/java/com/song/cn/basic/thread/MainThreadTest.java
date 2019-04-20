package com.song.cn.basic.thread;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主要用于测试：当主线程退出，是否意味着程序退出。
 * 结论：主线程退出，不代表程序退出，即主线程并不一定是最后一个退出的线程。
 * 知识点：
 * JVM会在以下情形时退出：
 * 1.没有非daemon线程正在运行；
 * 2.所有线程都是daemon线程；
 * 3.显式调用exit方法
 * <p>
 * 另外：对于长期的定时的线程，最好设置为daemon线程。防止程序无法完全退出的现象出现
 */
public class MainThreadTest {
    public static void main(String[] args) throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                System.out.println(new Date().toString() + ": Timer thread is running...");
            }

        }, 1000, 1000);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Second thread started");
                    try {
                        Thread.sleep(5000);  // wait two seconds
                    } catch (Exception e) {

                    } finally {
                        System.out.println("Second thread (almost) finished");
                    }
                }
            }
        });
//        thread.setDaemon(true);
        thread.start();

        System.out.println("Main thread ends!");
//        Thread.sleep(10000);
        //添加之后，子线程会退出
        timer.cancel();
    }
}
