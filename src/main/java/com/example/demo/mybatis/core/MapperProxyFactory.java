package com.example.demo.mybatis.core;

import com.example.demo.mybatis.core.anno.Param;
import com.example.demo.mybatis.core.anno.Select;
import com.example.demo.mybatis.core.handler.IntegerTypeHandler;
import com.example.demo.mybatis.core.handler.StringTypeHandler;
import com.example.demo.mybatis.core.handler.TypeHandler;
import com.example.util.LogUtils;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/**
 * // 1 load JDBC driver
 * Class.forName(driverName);
 * // 2 establish db connect
 * Connection connection = DriverManager.getConnection(url, username, password);
 * // 3 get statement
 * String sql = "select * from users where password = ?";
 * PreparedStatement preparedStatement = connection.prepareStatement(sql);
 * // the first parameter is 1, the second is 2, ...
 * preparedStatement.setString(1, "pass");
 * // 4 execute sql
 * preparedStatement.execute();
 * List<User> userList = new ArrayList<>();
 * // 5 handle result set
 * ResultSet resultSet = preparedStatement.getResultSet();
 * while (resultSet.next()) {
 *     User user = new User();
 *     user.setId(resultSet.getInt("id"));
 *     user.setUsername(resultSet.getString("username"));
 *     user.setPassword(resultSet.getString("password"));
 *     user.setAge(resultSet.getInt("age"));
 *     userList.add(user);
 * }
 * connection.close();
 * return userList
 */
public class MapperProxyFactory {

    public static Map<Class, TypeHandler> typeHandlerMap = new HashMap<>(16);

    static {
        typeHandlerMap.put(String.class, new StringTypeHandler());
        typeHandlerMap.put(int.class, new IntegerTypeHandler());
        typeHandlerMap.put(Integer.class, new IntegerTypeHandler());
    }


    public static <T> T getMapper(Class<T> mapper) {

        Object proxyInstance = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{mapper}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 1 get connection from connection pool
                Connection connection = getConnection();

                // 2 get sql from annotation
                Select annotation = method.getAnnotation(Select.class);
                String sql = annotation.value(); // select * from users where id = #{id} and username = #{username} and age > #{id}

                // 3 key: value mapping
                // "id": 15
                // "username": "huangzhikang"
                // when jdk is 1.7,  method.getParameters()[i].getName() is args1, args2 ...
                HashMap<String, Object> paramsMapping = new HashMap<>();
                for (int i = 0; i < method.getParameters().length; i++) {
                    paramsMapping.put(method.getParameters()[i].getName(), args[i]);
                    LogUtils.debug("{}: {}", method.getParameters()[i].getName(), args[i]);
                    paramsMapping.put(method.getParameters()[i].getAnnotation(Param.class).value(), args[i]);
                    LogUtils.debug("@param {}: {}", method.getParameters()[i].getAnnotation(Param.class).value(), args[i]);
                }
                // printMap(paramsMapping);

                // 4 parse sql
                // list-0: id
                // list-1: username
                // list-2: select * from users where id = ? and username = ?
                List<String> paramsListAndProcessedSQL = JDBCUtils.parseSQL(sql);
                // printList(paramsListAndProcessedSQL);
                List<String> fillParamsList = paramsListAndProcessedSQL.subList(0, paramsListAndProcessedSQL.size() - 1);
                String processedSQL = paramsListAndProcessedSQL.get(paramsListAndProcessedSQL.size() - 1); // get last element is sql
                LogUtils.debug("fill params list: {}", fillParamsList.toString());
                LogUtils.debug("processed sql: {}", processedSQL);

                // 5 fill params
                PreparedStatement preparedStatement = connection.prepareStatement(processedSQL);
                for (int i = 0; i < fillParamsList.size(); i++) {
                    // needFillSqlName - paramValue - paramType
                    String needFillSqlName = fillParamsList.get(i);
                    Object paramValue = paramsMapping.get(needFillSqlName);
                    Class<?> paramType = paramValue.getClass();

                    // preparedStatement.setParamType(index, paramValue);
                    LogUtils.debug("need param: {}, value: {}, type: {}", needFillSqlName, paramValue, paramType);
                    typeHandlerMap.get(paramType).setParameter(preparedStatement, i + 1, paramValue);

                }

                // 6 execute sql
                preparedStatement.execute();

                // 7 get return type, List<User> or User or List<Order> ...
                Class returnType = null;
                boolean isList = false;

                Class returnTypeClass = method.getReturnType();
                Type genericReturnType = method.getGenericReturnType();
                LogUtils.debug("method.getReturnType(): {}", returnTypeClass.toString());
                LogUtils.debug("method.getGenericReturnType(): {}", genericReturnType.toString());

                if (genericReturnType instanceof Class<?>) {
                    // no generic - return User
                    returnType = (Class) genericReturnType;
                } else if (genericReturnType instanceof ParameterizedType) {
                    // is generic - return List<User> - get User type
                    Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                    returnType = (Class) actualTypeArguments[0];
                    isList = true;
                }


                // 8 mapping setter methods
                HashMap<String, Method> setterMethodMapping = new HashMap<>();
                for (Method declaredMethod : returnType.getDeclaredMethods()) {
                    if (declaredMethod.getName().startsWith("set")) {
                        String propertyName = declaredMethod.getName().substring(3);
                        propertyName = propertyName.substring(0, 1).toLowerCase(Locale.ROOT) + propertyName.substring(1);
                        setterMethodMapping.put(propertyName, declaredMethod);
                    }
                }
                LogUtils.debug("setterMethodMapping: {}", setterMethodMapping.toString());

                // 9 handle result set and package to pojo
                List<Object> resultList = new ArrayList<>();

                ResultSet resultSet = preparedStatement.getResultSet();
                ResultSetMetaData metaData = preparedStatement.getMetaData();
                // get column name from mysql db, no process for user_name to userName
                List<String> columnList = new ArrayList<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    columnList.add(metaData.getColumnName(i + 1));
                }
                LogUtils.debug("mysql column: {}", columnList.toString());

                while (resultSet.next()) {
                    // line data mapping pojo
                    // Object instance = returnType.newInstance();
                    Object instance = returnType.getDeclaredConstructor().newInstance();

                    // loop invoke setter method to fill result
                    for (int i = 0; i < columnList.size(); i++) {
                        String columnName = columnList.get(i);
                        Method setterMethod = setterMethodMapping.get(columnName);
                        // LogUtils.debug("call setter is: {}", setterMethod.toString());
                        // LogUtils.debug("call setter param type is: {}", setterMethod.getParameterTypes()[0]);
                        TypeHandler typeHandler = typeHandlerMap.get(setterMethod.getParameterTypes()[0]);
                        // LogUtils.debug("typeHandler: {}", typeHandler.toString());
                        setterMethod.invoke(instance, typeHandler.getResult(resultSet, columnName));
                    }
                    // LogUtils.debug("instance: {}", instance.toString());
                    resultList.add(instance);
                }

                // 10 return data
                if (!isList) {
                    return resultList.get(0);
                }
                return resultList;
            }
        });

        return (T) proxyInstance;
    }

    private static Connection getConnection() {
        try {
            // load JDBC info
            Map<String, String> jdbcMap = JDBCUtils.readJDBCInfo("src/main/java/com/example/demo/mybatis/jdbc.properties");
            String url = jdbcMap.get(JDBCUtils.JDBC_URL);
            String username = jdbcMap.get(JDBCUtils.JDBC_USERNAME);
            String password = jdbcMap.get(JDBCUtils.JDBC_PASSWORD);
            String driverName = jdbcMap.get(JDBCUtils.JDBC_DRIVER_CLASS_NAME);

            // 1 load JDBC driver
            Class.forName(driverName);
            // 2 establish db connect

            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void printMap(Map<String, Object> map) {
        map.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    private static void printList(List<String> list) {
        list.forEach(v -> System.out.println("@List: " + v));
    }
}
