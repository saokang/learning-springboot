#!/bin/bash

# 设置颜色代码
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# 检查是否有管理员权限
check_root() {
    if [ "$(id -u)" != "0" ]; then
        echo -e "${RED}错误：需要以管理员权限运行此脚本。${NC}"
        exit 1
    fi
}

# 检查 Redis 安装包是否存在并解压
check_redis_package() {
    local redis_package_file="$1"

    if [ ! -f "$redis_package_file" ]; then
        echo -e "${RED}错误：Redis 安装包文件不存在。${NC}"
        exit 1
    fi

    local redis_package_dir="${redis_package_file%.tar.gz}"
    if [ ! -d "$redis_package_dir" ]; then
        echo -e "${GREEN}正在解压 Redis 安装包...${NC}"
        tar -xf "$redis_package_file"
    fi
}

# 检查目录是否存在，不存在则创建
check_directory() {
    if [ ! -d "$1" ]; then
        mkdir -p "$1"
    fi
}

# 安装 Redis
install_redis() {
    local port=$1
    local role=$2
    local redis_package_dir=$3

    echo -e "${GREEN}正在安装 Redis $role (端口号：$port)...${NC}"
    check_directory "redis_$role$port"
    cp -r "$redis_package_dir"/* "redis_$role$port"
    sed -i "s/port 6379/port $port/" "redis_$role$port/redis.conf"
    sed -i "s/^# cluster-enabled yes/cluster-enabled yes/" "redis_$role$port/redis.conf" # 启用集群模式
    sed -i "s/^# cluster-config-file nodes-6379.conf/cluster-config-file nodes-$port.conf/" "redis_$role$port/redis.conf" # 指定集群配置文件
    echo -e "${GREEN}Redis $role (端口号：$port) 安装完成。${NC}"
}

# 卸载 Redis
uninstall_redis() {
    local role=$1

    echo -e "${GREEN}正在卸载 Redis $role...${NC}"
    for dir in "redis_$role"*; do
        if [ -d "$dir" ]; then
            rm -rf "$dir"
            echo -e "${GREEN}成功卸载 $dir。${NC}"
        fi
    done
}

# 显示 Redis 配置
show_redis_configuration() {
    echo -e "${GREEN}Redis 配置信息：${NC}"
    for dir in redis_master* redis_slave*; do
        if [ -d "$dir" ]; then
            echo -e "${GREEN}目录：$dir${NC}"
            echo -e "${GREEN}配置文件位置：$dir/redis.conf${NC}"
            echo -e "${GREEN}运行端口：$(grep -oP '(?<=^port )\d+' $dir/redis.conf)${NC}"
        fi
    done
}

# 主函数
main() {
    check_root

    if [ "$#" -lt 2 ]; then
        echo -e "${RED}错误：请提供至少两个参数。${NC}"
        echo -e "用法：$0 <redis-package> <install | uninstall> [options]"
        exit 1
    fi

    local redis_package="$1"
    local operation="$2"
    shift 2

    case "$operation" in
        install)
            check_redis_package "$redis_package"
            if [ "$#" -lt 3 ]; then
                echo -e "${RED}错误：请提供正确的参数。${NC}"
                echo -e "用法：$0 <redis-package> install <master-port> <slave1-port> <slave2-port>"
                exit 1
            fi
            install_redis "$@"
            show_redis_configuration
            ;;
        uninstall)
            uninstall_redis "master"
            uninstall_redis "slave"
            ;;
        *)
            echo -e "${RED}错误：未提供正确的操作参数。${NC}"
            echo -e "用法：$0 <redis-package> <install | uninstall> [options]"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
