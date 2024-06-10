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

# 检查是否有网络连接
check_internet() {
    if ping -q -c 1 -W 1 baidu.com >/dev/null; then
        return 0
    else
        return 1
    fi
}

# 安装 Docker (Ubuntu)
install_docker_ubuntu() {
    echo -e "${GREEN}正在安装 Docker (Ubuntu)...${NC}"
    sudo apt-get update
    sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
    sudo apt-get update
    sudo apt-get install -y docker-ce
    sudo systemctl start docker
    sudo systemctl enable docker
    echo -e "${GREEN}Docker 安装完成 (Ubuntu)。${NC}"
}

# 安装 Docker (CentOS)
install_docker_centos() {
    echo -e "${GREEN}正在安装 Docker (CentOS)...${NC}"
    sudo yum install -y yum-utils
    sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    sudo yum install -y docker-ce docker-ce-cli containerd.io
    sudo systemctl start docker
    sudo systemctl enable docker
    echo -e "${GREEN}Docker 安装完成 (CentOS)。${NC}"
}

# 卸载 Docker (Ubuntu)
uninstall_docker_ubuntu() {
    echo -e "${GREEN}正在卸载 Docker (Ubuntu)...${NC}"
    sudo apt-get purge -y docker-ce docker-ce-cli containerd.io
    sudo rm -rf /var/lib/docker
    sudo rm -rf /var/lib/containerd
    echo -e "${GREEN}Docker 已成功卸载 (Ubuntu)。${NC}"
}

# 卸载 Docker (CentOS)
uninstall_docker_centos() {
    echo -e "${GREEN}正在卸载 Docker (CentOS)...${NC}"
    sudo yum remove -y docker-ce docker-ce-cli containerd.io
    sudo rm -rf /var/lib/docker
    sudo rm -rf /var/lib/containerd
    echo -e "${GREEN}Docker 已成功卸载 (CentOS)。${NC}"
}

# 设置 Docker 镜像源为阿里云
set_docker_mirror() {
    if [ -z "$1" ]; then
        echo -e "${RED}错误：请提供镜像源地址。${NC}"
        echo -e "用法：$0 set-mirror <${GREEN}mirror-url${NC}>"
        exit 1
    fi

    local mirror_url="$1"
    echo -e "${GREEN}正在设置 Docker 镜像源为 ${mirror_url}...${NC}"
    sudo mkdir -p /etc/docker
    sudo tee /etc/docker/daemon.json > /dev/null <<EOL
{
    "registry-mirrors": ["${mirror_url}"]
}
EOL
    sudo systemctl daemon-reload
    sudo systemctl restart docker
    echo -e "${GREEN}Docker 镜像源设置完成。${NC}"
}

# 主函数
main() {
    check_root

    if [ "$1" == "install" ]; then
        if check_internet; then
            if command -v apt-get &>/dev/null; then
                install_docker_ubuntu
            elif command -v yum &>/dev/null; then
                install_docker_centos
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
            uninstall_docker_ubuntu
        elif command -v yum &>/dev/null; then
            uninstall_docker_centos
        else
            echo -e "${RED}错误：未找到适用的包管理器。${NC}"
            exit 1
        fi
    elif [ "$1" == "set-mirror" ]; then
        shift
        set_docker_mirror "$@"
    else
        echo -e "${RED}错误：未提供正确的操作参数。${NC}"
        echo -e "用法：$0 <${GREEN}install | uninstall | set-mirror${NC}> [mirror-url]"
        exit 1
    fi
}

# 执行主函数
main "$@"
