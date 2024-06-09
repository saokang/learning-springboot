package com.example.demo.learning04_mybatis.core;

import com.example.demo.learning04_mybatis.pojo.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Test User Table
 */
public class JDBCUtils {

    public static final String JDBC_URL = "jdbc.url";
    public static final String JDBC_USERNAME = "jdbc.username";
    public static final String JDBC_PASSWORD = "jdbc.password";
    public static final String JDBC_DRIVER_CLASS_NAME = "jdbc.driverClassName";

    /**
     * read info from properties
     *
     * @param propertiesPath path
     * @return map
     */
    public static Map<String, String> readJDBCInfo(String propertiesPath) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String url = properties.getProperty(JDBC_URL);
        String username = properties.getProperty(JDBC_USERNAME);
        String password = properties.getProperty(JDBC_PASSWORD);
        String driver = properties.getProperty(JDBC_DRIVER_CLASS_NAME);
        HashMap<String, String> map = new HashMap<>();
        map.put(JDBC_URL, url);
        map.put(JDBC_USERNAME, username);
        map.put(JDBC_PASSWORD, password);
        map.put(JDBC_DRIVER_CLASS_NAME, driver);
        return map;
    }

    /**
     * convert myBatis SQL to Mysql SQL
     *
     * @param myBatisSql select * from users where id = #{id} and username = #{username} and age > #{id}
     * @return ["id", "username", "id", "select * from users where id = ? and username = ? and age > ?"]
     */
    public static List<String> parseSQL(String myBatisSql) {
        // 使用正则表达式提取 {} 中的内容
        Pattern pattern = Pattern.compile("\\#\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(myBatisSql);
        LinkedList<String> placeholders = new LinkedList<>();
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            placeholders.add(placeholder);
        }

        // 将 #{} 替换为 ?
        String sql = myBatisSql.replaceAll("\\#\\{(.*?)\\}", "?");
        placeholders.add(sql);
        return placeholders;
    }

    /**
     * JDBC sample - initialize.sql
     */
    public static void main(String[] args) {
        // load JDBC info
        Map<String, String> jdbcMap = readJDBCInfo("src/main/java/com/example/demo/mybatis/jdbc.properties");
        String url = jdbcMap.get(JDBC_URL);
        String username = jdbcMap.get(JDBC_USERNAME);
        String password = jdbcMap.get(JDBC_PASSWORD);
        String driverName = jdbcMap.get(JDBC_DRIVER_CLASS_NAME);
        jdbcMap.forEach((k, v) -> System.out.println(k + ": " + v));

        try {
            // 1 load JDBC driver
            Class.forName(driverName);

            // 2 establish db connect
            Connection connection = DriverManager.getConnection(url, username, password);

            // 3 get statement
            String sql = "select * from users where password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // the first parameter is 1, the second is 2, ...
            preparedStatement.setString(1, "pass");

            // 4 execute sql
            preparedStatement.execute();

            List<User> userList = new ArrayList<>();

            // 5 handle result set
            ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setAge(resultSet.getInt("age"));
                userList.add(user);
            }

            connection.close();

            userList.forEach(System.out::println);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
