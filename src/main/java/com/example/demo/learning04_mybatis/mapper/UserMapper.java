package com.example.demo.learning04_mybatis.mapper;

import com.example.demo.learning04_mybatis.core.anno.Param;
import com.example.demo.learning04_mybatis.core.anno.Select;
import com.example.demo.learning04_mybatis.pojo.User;

import java.util.List;

public interface UserMapper {

    @Select("select * from users where id = #{id} and username = #{username}")
    List<User> getUserListByIdAndName(@Param("id") Integer id, @Param("username") String username);

    @Select("select * from users where id = #{id}")
    User getUser(@Param("id") Integer id);

    @Select("select * from users")
    List<User> getUserList();

    @Select("select * from users where age < #{id}")
    List<User> getUserListByIdLtAge(@Param("id") Integer id);


}
