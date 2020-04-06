package com.song.cn.agent;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //Example 1 和 4
/*        Thread.sleep(1000);
        System.out.println("TestMain Main!");*/
        //Example 2
        //System.out.println(new TestClass().getNumber());
        //Example 3


        //Example 4
        System.out.println(new TestClass().getNumber());
        while (true) {
            Thread.sleep(1000); // 注意，这里是新建TestClass对象
            System.out.println(new TestClass().getNumber());
        }

    }
}
