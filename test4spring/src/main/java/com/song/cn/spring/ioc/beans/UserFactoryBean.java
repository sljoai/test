package com.song.cn.spring.ioc.beans;

import com.song.cn.spring.ioc.domain.User;
import org.springframework.beans.factory.FactoryBean;

/**
 * {@link com.song.cn.spring.ioc.domain.User}  Bean的 {@FactoryBean}的实现
 */
public class UserFactoryBean implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        return User.createUser();
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }
}
