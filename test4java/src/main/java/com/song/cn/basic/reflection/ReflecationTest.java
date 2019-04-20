package com.song.cn.basic.reflection;

public class ReflecationTest {

    public static void main(String[] args) throws ClassNotFoundException {
        // 对于2个String类型对象，它们的Class对象相同
        Class c1 = "Carson".getClass();
        Class c2 = Class.forName("java.lang.String");
        // 用==运算符实现两个类对象地址的比较
        System.out.println(c1 == c2);
        // 输出结果：true
    }
}
