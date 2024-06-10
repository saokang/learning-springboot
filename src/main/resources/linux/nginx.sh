#!/bin/bash

# 设置颜色代码
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Nginx 配置文件位置
NGINX_CONF="/etc/nginx/nginx.conf"

# Nginx 访问日志位置
NGINX_ACCESS_LOG="/var/log/nginx/access.log"

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

# 安装 Nginx (Ubuntu)
install_nginx_ubuntu() {
    echo -e "${GREEN}正在安装 Nginx (Ubuntu)...${NC}"
    sudo apt-get update
    sudo apt-get install -y nginx
    sudo systemctl start nginx
    sudo systemctl enable nginx
    echo -e "${GREEN}Nginx 安装完成 (Ubuntu)。${NC}"
}

# 安装 Nginx (CentOS)
install_nginx_centos() {
    echo -e "${GREEN}正在安装 Nginx (CentOS)...${NC}"
    sudo yum install -y epel-release
    sudo yum install -y nginx
    sudo systemctl start nginx
    sudo systemctl enable nginx
    echo -e "${GREEN}Nginx 安装完成 (CentOS)。${NC}"
}

# 卸载 Nginx (Ubuntu)
uninstall_nginx_ubuntu() {
    echo -e "${GREEN}正在卸载 Nginx (Ubuntu)...${NC}"
    sudo systemctl stop nginx
    sudo apt-get purge -y nginx nginx-common
    sudo apt-get autoremove -y
    echo -e "${GREEN}Nginx 已成功卸载 (Ubuntu)。${NC}"
}

# 卸载 Nginx (CentOS)
uninstall_nginx_centos() {
    echo -e "${GREEN}正在卸载 Nginx (CentOS)...${NC}"
    sudo systemctl stop nginx
    sudo yum remove -y nginx
    echo -e "${GREEN}Nginx 已成功卸载 (CentOS)。${NC}"
}

# 重启 Nginx
restart_nginx() {
    echo -e "${GREEN}正在重启 Nginx...${NC}"
    sudo systemctl restart nginx
    echo -e "${GREEN}Nginx 已重启。${NC}"
}

# 显示 Nginx 配置文件位置
show_nginx_configuration_location() {
    echo -e "${GREEN}Nginx 配置文件位置：${NGINX_CONF}${NC}"
}

# 显示 Nginx 访问日志位置
show_nginx_access_log_location() {
    echo -e "${GREEN}Nginx 访问日志位置：${NGINX_ACCESS_LOG}${NC}"
}

# 主函数
main() {
    check_root

    if [ "$1" == "install" ]; then
        if check_internet; then
            if command -v apt-get &>/dev/null; then
                install_nginx_ubuntu
            elif command -v yum &>/dev/null; then
                install_nginx_centos
            else
                echo -e "${RED}错误：未找到适用的包管理器。${NC}"
                exit 1
            fi
        else
            echo -e "${RED}错误：未检测到网络连接，请检查网络并重试。${NC}"
            exit 1
        fi
    elif [ "$1" == "uninstall" ]; then
        if command -v apt-get &>/dev/null; then
            uninstall_nginx_ubuntu
        elif command -v yum &>/dev/null; then
            uninstall_nginx_centos
        else
            echo -e "${RED}错误：未找到适用的包管理器。${NC}"
            exit 1
        fi
    elif [ "$1" == "restart" ]; then
        restart_nginx
    elif [ "$1" == "conf" ]; then
        show_nginx_configuration_location
    elif [ "$1" == "log" ]; then
        show_nginx_access_log_location
    else
        echo -e "${RED}错误：未提供正确的操作参数。${NC}"
        echo -e "用法：$0 <${GREEN}install | uninstall | restart | conf | log${NC}>"
        exit 1
    fi
}

# 执行主函数
main "$@"
