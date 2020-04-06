package com.song.cn.basic.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxyExample {
    static interface Car {
        void running();
    }

    static class Bus implements Car {

        @Override
        public void running() {
            System.out.println("The bus is running.");
        }
    }

    static class Taxi implements Car {

        @Override
        public void running() {
            System.out.println("The taxi is running.");
        }
    }

    /**
     * JDK Proxy
     */
    static class JDKProxy implements InvocationHandler {
        //代理对象
        private Object target;

        public Object getInstance(Object target) {
            this.target = target;
            return Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(), this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("动态代理之前的业务处理");
            // 执行调度方法（此方法执行前后，可以进行相关业务处理）
            Object result = method.invoke(target, args);
            return result;
        }
    }

    public static void main(String[] args) {
        JDKProxy jdkProxy = new JDKProxy();
        Car car = (Car) jdkProxy.getInstance(new Taxi());
        car.running();
    }
}
