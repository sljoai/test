package com.song.cn.agent;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class TimeInterceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<?> callable) throws Exception {
        long start = System.currentTimeMillis();
        Thread.sleep(1000);
        try {
            return callable.call();//执行原函数(被拦截的目标函数方法)
        } finally {
            System.out.println(method.getName() + " :"
                    + (System.currentTimeMillis() - start) + " ms");
        }
    }
}
