#!/bin/bash

# 设置颜色代码
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Tomcat 安装路径
TOMCAT_INSTALL_DIR="/opt/tomcat"

# Tomcat 日志文件位置
TOMCAT_LOG_DIR="$TOMCAT_INSTALL_DIR/logs"

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

# 查询可用的 Tomcat 版本
list_tomcat_versions() {
    echo -e "${GREEN}可用的 Tomcat 版本：${NC}"
    curl -s https://tomcat.apache.org/download-90.cgi | grep -oP 'v[0-9]+\.[0-9]+\.[0-9]+' | sort -u
}

# 安装 Tomcat
install_tomcat() {
    list_tomcat_versions

    echo -e "${GREEN}请输入要安装的 Tomcat 版本（例如 9.0.56）：${NC}"
    read -r version

    TOMCAT_URL="https://downloads.apache.org/tomcat/tomcat-9/v$version/bin/apache-tomcat-$version.tar.gz"

    echo -e "${GREEN}正在安装 Tomcat $version...${NC}"

    if check_internet; then
        wget $TOMCAT_URL -O /tmp/apache-tomcat-$version.tar.gz
        sudo mkdir -p $TOMCAT_INSTALL_DIR
        sudo tar -xzf /tmp/apache-tomcat-$version.tar.gz -C $TOMCAT_INSTALL_DIR --strip-components=1
        sudo chmod +x $TOMCAT_INSTALL_DIR/bin/*.sh
        sudo $TOMCAT_INSTALL_DIR/bin/startup.sh

        echo -e "${GREEN}Tomcat $version 安装完成。${NC}"
    else
        echo -e "${RED}错误：未检测到网络连接，请检查网络并重试。${NC}"
        exit 1
    fi
}

# 卸载 Tomcat
uninstall_tomcat() {
    echo -e "${GREEN}正在卸载 Tomcat...${NC}"
    sudo $TOMCAT_INSTALL_DIR/bin/shutdown.sh
    sudo rm -rf $TOMCAT_INSTALL_DIR
    echo -e "${GREEN}Tomcat 已成功卸载。${NC}"
}

# 显示 Tomcat 安装位置
show_tomcat_installation_location() {
    echo -e "${GREEN}Tomcat 安装位置：${TOMCAT_INSTALL_DIR}${NC}"
}

# 显示 Tomcat 日志文件位置
show_tomcat_log_location() {
    echo -e "${GREEN}Tomcat 日志文件位置：${TOMCAT_LOG_DIR}${NC}"
}

# 主函数
main() {
    check_root

    case "$1" in
        install)
            install_tomcat
            ;;
        uninstall)
            uninstall_tomcat
            ;;
        location)
            show_tomcat_installation_location
            ;;
        log)
            show_tomcat_log_location
            ;;
        *)
            echo -e "${RED}错误：未提供正确的操作参数。${NC}"
            echo -e "用法：$0 <${GREEN}install | uninstall | location | log${NC}>"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
