package com.song.cn.sw.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.song.cn.sw.api.HelloService;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class HelloWorldController {
    @Reference
    private HelloService helloService;

    @Trace
    @GetMapping("hello/{words}")
    public String hello(@PathVariable("words") String words) throws Exception {
        Thread.sleep(1000);
        System.out.println("traceId:" + TraceContext.traceId());
        ActiveSpan.tag("hello-trace", words);
        String say = helloService.say(words);
        Thread.sleep(1000);
        return say;
    }

    @Trace
    @GetMapping("/err")
    public String err() {
        String traceId = TraceContext.traceId();
        ActiveSpan.tag("traceId:{}", traceId);
        throw new RuntimeException("err");
    }
}
