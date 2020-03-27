package com.song.cn.spring.ioc.beans;

import com.song.cn.spring.ioc.domain.User;

/**
 * User 工厂类
 */
public interface UserFactory {
    //提供默认实现
    default User createUser() {
        return User.createUser();
    }
}
