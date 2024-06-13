package com.example.demo.mybatis;

import com.example.demo.mybatis.core.MapperProxyFactory;
import com.example.demo.mybatis.mapper.UserMapper;

public class Main {

    public static void main(String[] args) {

        UserMapper userMapper = MapperProxyFactory.getMapper(UserMapper.class);

        userMapper.getUserList().forEach(System.out::println);

        userMapper.getUserListByIdAndName(2, "huangzhikang").forEach(System.out::println);

        System.out.println(userMapper.getUser(5));

        userMapper.getUserListByIdLtAge(20).forEach(System.out::println);

    }
}
