package com.example.demo.mybatis.core.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeHandler<T> {

    void setParameter(PreparedStatement statement, int index, T value) throws SQLException;

    T getResult(ResultSet rs, String columnName) throws SQLException;
}
