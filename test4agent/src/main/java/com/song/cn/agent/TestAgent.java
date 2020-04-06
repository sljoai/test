package com.song.cn.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class TestAgent {
    //Example 1
    /*public static void premain(String agentArgs,
                               Instrumentation inst){
        System.out.println("this is a java agent with two args");
        System.out.println("参数:" + agentArgs + "\n");
    }

    public static void premain(String agentArgs){
        System.out.println("this is a java agent only one args");
        System.out.println("参数:" + agentArgs + "\n");
    }*/

    //Example 2
/*    public static void premain(String agentArgs,
                               Instrumentation inst) throws UnmodifiableClassException {
        inst.addTransformer(new Transformer(),true);
        inst.retransformClasses(TestClass.class);
        System.out.println("Premain done");
    }*/

    // Example 3
/*    public static void premain(String agentArgs,
                               Instrumentation inst){
        AgentBuilder.Transformer transformer =
                new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                                            TypeDescription typeDescription,
                                                            ClassLoader classLoader,
                                                            JavaModule javaModule) {
                        return builder
                                //method指定哪些方法需要被拦截，
                                // ElementMathers.any()表示拦截所有的方法
                                .method(
                                //ToDO: 这是一种什么样的结构数据呢？
                                ElementMatchers.<MethodDescription>any())
                                // intercept() 指明拦截上述方法的拦截器
                                .intercept(MethodDelegation.to(TimeInterceptor.class));
                    }
                };
        new AgentBuilder
                .Default()
                // 根据包名前缀拦截类
                .type(ElementMatchers.nameStartsWith("com.song.cn.agent"))
                // 拦截到的类由 transformer 处理
                .transform(transformer)
                .installOn(inst);// 安装到 Instrumentation
    }*/

    public static void agentmain(String agentArgs,
                                 Instrumentation inst) throws UnmodifiableClassException {
        // 注册一个 Transformer，该 Transformer在类加载时被调用
        inst.addTransformer(new Transformer(), true);
        inst.retransformClasses(TestClass.class);
        System.out.println("premain done");

    }

}
