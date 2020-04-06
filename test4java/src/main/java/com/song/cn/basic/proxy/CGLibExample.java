package com.song.cn.basic.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLibExample {

    static class Car {
        public void running() {
            System.out.println("The car is running.");
        }
    }

    static class CGLibProxy implements MethodInterceptor {
        // 代理对象
        private Object target;

        public Object getInstance(Object target) {
            this.target = target;
            Enhancer enhancer = new Enhancer();
            //设置父类为实例类
            enhancer.setSuperclass(this.target.getClass());
            // 回调方法
            enhancer.setCallback(this);
            return enhancer.create();
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            System.out.println("方法调用前业务处理.");
            //  执行调度方法（此方法执行前后，可以进行相关业务处理）
            Object result = methodProxy.invoke(o, objects);
            return result;
        }
    }

    public static void main(String[] args) {
        CGLibProxy proxy = new CGLibProxy();
        Car car = (Car) proxy.getInstance(new Car());
        car.running();
    }

}
