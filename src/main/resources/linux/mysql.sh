#!/bin/bash

# 设置颜色代码
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# MySQL 安装路径
MYSQL_INSTALL_DIR="/usr/local/mysql"

# MySQL 配置文件位置
MYSQL_CONF="/etc/my.cnf"

# MySQL 日志文件位置
MYSQL_LOG="/var/log/mysql/mysql.log"

# MySQL 初始 root 密码
MYSQL_ROOT_PASSWORD="123456"

# 检查是否有管理员权限
check_root() {
    if [ "$(id -u)" != "0" ]; then
        echo -e "${RED}错误：需要以管理员权限运行此脚本。${NC}"
        exit 1
    fi
}

# 检查是否有网络连接
check_internet() {
    if ping -q -c 1 -W 1 baidu.com >/dev/null; then
        return 0
    else
        return 1
    fi
}

# 生成自定义 my.cnf 配置文件
generate_my_cnf() {
    sudo tee $MYSQL_CONF > /dev/null <<EOL
# MySQL configuration file

[client]
# 客户端默认设置
port = 3306
socket = /var/lib/mysql/mysql.sock

[mysqld]
# MySQL 服务全局设置
user = mysql
port = 3306
socket = /var/lib/mysql/mysql.sock
pid-file = /var/run/mysqld/mysqld.pid
basedir = /usr/local/mysql
datadir = /var/lib/mysql
tmpdir = /tmp
lc-messages-dir = /usr/share/mysql
skip-external-locking

# 允许外部连接
bind-address = 0.0.0.0

# 日志设置
log-error = /var/log/mysql/error.log
slow-query-log = 1
slow-query-log-file = /var/log/mysql/mysql-slow.log
long_query_time = 2

# 服务优化
key_buffer_size = 16M
max_allowed_packet = 16M
thread_stack = 192K
thread_cache_size = 8
myisam-recover-options = BACKUP
max_connections = 100
table_open_cache = 64
thread_concurrency = 10

# 缓存和临时表设置
query_cache_limit = 1M
query_cache_size = 16M
tmp_table_size = 16M

# 二进制日志设置
log_bin = /var/log/mysql/mysql-bin.log
expire_logs_days = 10
max_binlog_size = 100M

# InnoDB 引擎设置
innodb_file_per_table = 1
innodb_buffer_pool_size = 128M
innodb_log_file_size = 50M
innodb_log_buffer_size = 8M
innodb_flush_log_at_trx_commit = 1

[mysqldump]
# mysqldump 设置
quick
quote-names
max_allowed_packet = 16M

[mysql]
# 客户端交互式命令行设置
# 设置自动断开时间
auto-rehash

[isamchk]
# isamchk 工具设置
key_buffer = 16M

!includedir /etc/mysql/conf.d/
EOL

    echo -e "${GREEN}自定义 my.cnf 配置文件已生成到 $MYSQL_CONF。${NC}"
}

# 安装 MySQL
install_mysql() {
    # 检查是否已安装 MySQL
    if [ -d "$MYSQL_INSTALL_DIR" ]; then
        echo -e "${RED}错误：MySQL 已经安装，请先卸载已有的 MySQL。${NC}"
        exit 1
    fi

    # 检查 MariaDB 依赖
    if command -v mariadb &>/dev/null; then
        echo -e "${GREEN}检测到 MariaDB 已安装，将先卸载 MariaDB。${NC}"
        sudo systemctl stop mariadb
        sudo yum remove -y mariadb mariadb-server
    fi

    # 在线安装 MySQL
    if check_internet; then
        echo -e "${GREEN}正在在线安装 MySQL...${NC}"
        # 在这里编写在线安装 MySQL 的代码，以下是一个示例
        if command -v yum &>/dev/null; then
            sudo yum install -y mysql-server
        elif command -v apt-get &>/dev/null; then
            sudo apt-get update
            sudo apt-get install -y mysql-server
        elif command -v zypper &>/dev/null; then
            sudo zypper install -y mysql-community-server
        else
            echo -e "${RED}错误：未找到适用的包管理器。${NC}"
            exit 1
        fi

        # 启动 MySQL 服务
        sudo systemctl start mysqld || sudo systemctl start mysql

        # 生成自定义配置文件
        generate_my_cnf

        # 重启 MySQL 服务以应用新配置
        sudo systemctl restart mysqld || sudo systemctl restart mysql

        # 设置初始 root 密码和安全配置
        echo -e "${GREEN}正在设置初始 root 密码和安全配置...${NC}"
        sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$MYSQL_ROOT_PASSWORD';"
        sudo mysql -e "DELETE FROM mysql.user WHERE User='';"
        sudo mysql -e "DROP DATABASE test;"
        sudo mysql -e "FLUSH PRIVILEGES;"

        echo -e "${GREEN}MySQL 安装完成并已应用自定义配置文件和初始密码。${NC}"
    else
        echo -e "${RED}错误：未检测到网络连接，请检查网络并重试。${NC}"
        exit 1
    fi
}

# 卸载 MySQL
uninstall_mysql() {
    # 检查是否已安装 MySQL
    if [ ! -d "$MYSQL_INSTALL_DIR" ]; then
        echo -e "${RED}错误：未找到已安装的 MySQL。${NC}"
        exit 1
    fi

    # 停止 MySQL 服务
    sudo systemctl stop mysqld || sudo systemctl stop mysql

    # 卸载 MySQL
    if command -v yum &>/dev/null; then
        sudo yum remove -y mysql-server
    elif command -v apt-get &>/dev/null; then
        sudo apt-get remove -y mysql-server
    elif command -v zypper &>/dev/null; then
        sudo zypper remove -y mysql-community-server
    fi

    # 删除 MySQL 安装目录
    sudo rm -rf $MYSQL_INSTALL_DIR

    echo -e "${GREEN}MySQL 已成功卸载。${NC}"
}

# 停止 MySQL
stop_mysql() {
    sudo systemctl stop mysqld || sudo systemctl stop mysql
    echo -e "${GREEN}MySQL 已停止。${NC}"
}

# 启动 MySQL
start_mysql() {
    sudo systemctl start mysqld || sudo systemctl start mysql
    echo -e "${GREEN}MySQL 已启动。${NC}"
}

# 重启 MySQL
restart_mysql() {
    sudo systemctl restart mysqld || sudo systemctl restart mysql
    echo -e "${GREEN}MySQL 已重启。${NC}"
}

# 查看 MySQL 版本
check_mysql_version() {
    mysql --version
}

# 显示 MySQL 安装位置
show_mysql_installation_location() {
    echo -e "${GREEN}MySQL 安装位置：$MYSQL_INSTALL_DIR${NC}"
}

# 显示 MySQL 配置文件信息
show_mysql_configuration() {
    echo -e "${GREEN}MySQL 配置文件位置：$MYSQL_CONF${NC}"
    echo -e "${GREEN}MySQL 配置文件内容：${NC}"
    cat $MYSQL_CONF
}

# 显示 MySQL 日志文件位置
show_mysql_log_location() {
    echo -e "${GREEN}MySQL 日志文件位置：$MYSQL_LOG${NC}"
}

# 执行用户选择的操作
case "$1" in
    install)
        check_root
        install_mysql
        ;;
    uninstall)
        check_root
        uninstall_mysql
        ;;
    stop)
        check_root
        stop_mysql
        ;;
    start)
        check_root
        start_mysql
        ;;
    restart)
        check_root
        restart_mysql
        ;;
    version)
        check_mysql_version
        ;;
    location)
        show_mysql_installation_location
        ;;
    config)
        show_mysql_configuration
        ;;
    log)
        show_mysql_log_location
        ;;
    *)
        echo -e "${RED}错误：未提供正确的操作参数。${NC}"
        echo -e "用法：$0 <${GREEN}install | uninstall | stop | start | restart | version | location | config | log${NC}>"
        exit 1
        ;;
esac
