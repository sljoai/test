package com.song.cn.sw.provider.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.song.cn.sw.api.HelloService;
import org.springframework.stereotype.Component;

@Component
@Service
public class DefaultHelloService implements HelloService {
    @Override
    public String say(String name) throws Exception {
        Thread.sleep(2000);
        return "hello" + name;
    }
}
