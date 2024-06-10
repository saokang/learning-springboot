#!/bin/bash

# 设置颜色代码
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Redis 配置文件位置
REDIS_CONF="/etc/redis/redis.conf"

# Redis 日志文件位置
REDIS_LOG="/var/log/redis/redis.log"

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

# 安装 Redis (Ubuntu)
install_redis_ubuntu() {
    echo -e "${GREEN}正在安装 Redis (Ubuntu)...${NC}"
    sudo apt-get update
    sudo apt-get install -y redis-server
    sudo systemctl start redis-server
    sudo systemctl enable redis-server
    echo -e "${GREEN}Redis 安装完成 (Ubuntu)。${NC}"
}

# 安装 Redis (CentOS)
install_redis_centos() {
    echo -e "${GREEN}正在安装 Redis (CentOS)...${NC}"
    sudo yum install -y epel-release
    sudo yum install -y redis
    sudo systemctl start redis
    sudo systemctl enable redis
    echo -e "${GREEN}Redis 安装完成 (CentOS)。${NC}"
}

# 卸载 Redis (Ubuntu)
uninstall_redis_ubuntu() {
    echo -e "${GREEN}正在卸载 Redis (Ubuntu)...${NC}"
    sudo systemctl stop redis-server
    sudo apt-get purge -y redis-server
    sudo apt-get autoremove -y
    echo -e "${GREEN}Redis 已成功卸载 (Ubuntu)。${NC}"
}

# 卸载 Redis (CentOS)
uninstall_redis_centos() {
    echo -e "${GREEN}正在卸载 Redis (CentOS)...${NC}"
    sudo systemctl stop redis
    sudo yum remove -y redis
    echo -e "${GREEN}Redis 已成功卸载 (CentOS)。${NC}"
}

# 重启 Redis
restart_redis() {
    echo -e "${GREEN}正在重启 Redis...${NC}"
    if command -v systemctl &>/dev/null; then
        sudo systemctl restart redis || sudo systemctl restart redis-server
    else
        sudo service redis restart || sudo service redis-server restart
    fi
    echo -e "${GREEN}Redis 已重启。${NC}"
}

# 显示 Redis 配置文件位置
show_redis_configuration_location() {
    echo -e "${GREEN}Redis 配置文件位置：${REDIS_CONF}${NC}"
}

# 显示 Redis 日志文件位置
show_redis_log_location() {
    echo -e "${GREEN}Redis 日志文件位置：${REDIS_LOG}${NC}"
}

# 主函数
main() {
    check_root

    case "$1" in
        install)
            if check_internet; then
                if command -v apt-get &>/dev/null; then
                    install_redis_ubuntu
                elif command -v yum &>/dev/null; then
                    install_redis_centos
                else
                    echo -e "${RED}错误：未找到适用的包管理器。${NC}"
                    exit 1
                fi
            else
                echo -e "${RED}错误：未检测到网络连接，请检查网络并重试。${NC}"
                exit 1
            fi
            ;;
        uninstall)
            if command -v apt-get &>/dev/null; then
                uninstall_redis_ubuntu
            elif command -v yum &>/dev/null; then
                uninstall_redis_centos
            else
                echo -e "${RED}错误：未找到适用的包管理器。${NC}"
                exit 1
            fi
            ;;
        restart)
            restart_redis
            ;;
        conf)
            show_redis_configuration_location
            ;;
        log)
            show_redis_log_location
            ;;
        *)
            echo -e "${RED}错误：未提供正确的操作参数。${NC}"
            echo -e "用法：$0 <${GREEN}install | uninstall | restart | conf | log${NC}>"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
