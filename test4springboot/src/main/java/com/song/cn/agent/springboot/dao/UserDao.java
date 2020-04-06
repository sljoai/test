package com.song.cn.agent.springboot.dao;

import com.song.cn.agent.springboot.bean.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {
    /**
     * 用户数据新增
     */
    @Insert("insert into t_user(id,name,age) values (#{id},#{name},#{age})")
    void addUser(User user);

    /**
     * 用户数据修改
     */
    @Update("update t_user set name=#{name},age=#{age} where id=#{id}")
    void updateUser(User user);

    /**
     * 用户数据删除
     */
    @Delete("delete from t_user where id=#{id}")
    void deleteUser(int id);

    /**
     * 根据用户名称查询用户信息
     */
    @Select("SELECT id,name,age FROM t_user where name=#{userName}")
    User findByName(@Param("userName") String userName);

    /**
     * 查询所有
     */
    @Select("SELECT id,name,age FROM t_user")
    List<User> findAll();

    /**
     * 根据用户ID查找用户
     *
     * @param id
     * @return
     */
    @Select("SELECT id,name,age FROM t_user where id=#{id}")
    User findUserById(@Param("id") int id);

    @Select("SELECT id,name,age FROM t_user where age=#{age}")
    List<User> findUserByAge(@Param("age") int age);

}
