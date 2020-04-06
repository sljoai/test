package com.song.cn.agent.springboot.service;

import com.song.cn.agent.springboot.bean.User;

import java.util.List;

public interface UserService {

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    boolean addUser(User user);

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    boolean updateUser(User user);


    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    boolean deleteUser(int id);

    /**
     * 根据用户名字查询用户信息
     *
     * @param userName
     */
    User findUserByName(String userName);


    /**
     * 查询所有
     *
     * @return
     */
    List<User> findAll();

    /**
     * 根据用户ID查找用于信息
     *
     * @param id
     * @return
     */
    User findUserById(int id);

    /**
     * 根据用户年龄查找
     *
     * @param userAge
     * @return
     */
    List<User> findUserByAge(int userAge);
}
