-- 删除数据库（如果存在）
DROP DATABASE IF EXISTS mybatis_code_analysis;

-- 创建数据库
CREATE DATABASE mybatis_code_analysis;

SHOW DATABASES;

-- 使用新创建的数据库
USE mybatis_code_analysis;

DROP TABLE IF EXISTS users;

-- 创建 users 表
-- UNIQUE (username): 确保用户名的唯一性。
-- UNIQUE INDEX idx_username (username): 在 username 字段上添加了唯一索引约束。
-- UNIQUE INDEX 关键字指定了唯一索引，并为该索引指定了名称 idx_username。索引类型默认为 B+ 树。
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    password VARCHAR(255) NOT NULL,
    age INT,
    UNIQUE (username)
) ENGINE=InnoDB;

-- 插入示例数据
INSERT INTO users (username, password, age) VALUES ('alice', UUID(), 25);
INSERT INTO users (username, password, age) VALUES ('bruce', UUID(), 30);
INSERT INTO users (username, password, age) VALUES ('clark', "pass", 35);
INSERT INTO users (username, password, age) VALUES ('diana', UUID(), 12);
INSERT INTO users (username, password, age) VALUES ('emily', "pass", 22);
INSERT INTO users (username, password, age) VALUES ('frank', UUID(), 40);
INSERT INTO users (username, password, age) VALUES ('grace', UUID(), 27);
INSERT INTO users (username, password, age) VALUES ('homer', "pass", 32);
INSERT INTO users (username, password, age) VALUES ('irene', UUID(), 29);
INSERT INTO users (username, password, age) VALUES ('jason', UUID(), 12);
INSERT INTO users (username, password, age) VALUES ('karen', UUID(), 31);
INSERT INTO users (username, password, age) VALUES ('leona', UUID(), 12);

SHOW TABLES;

select * from users;