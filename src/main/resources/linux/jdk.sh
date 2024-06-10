#!/bin/bash

# 设置颜色代码
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# 设置 JDK 安装路径
JDK_INSTALL_DIR="/usr/local/java"

# 检查传入的参数数量
if [ $# -lt 1 ]; then
    echo -e "${RED}错误：请提供 'install'、'uninstall' 或 'online' 作为参数。${NC}"
    echo -e "用法：$0 <${GREEN}install | uninstall | online${NC}> [jdk_archive_filename]"
    exit 1
fi

# 检查是否有管理员权限
if [ "$(id -u)" != "0" ]; then
    echo -e "${RED}错误：需要以管理员权限运行此脚本。${NC}"
    exit 1
fi

# 检查网络连接
check_internet() {
    if ping -q -c 1 -W 1 baidu.com >/dev/null; then
        return 0
    else
        return 1
    fi
}

# 安装 JDK
install_jdk() {
    # 检查是否已经安装了 JDK
    if [ -d "$JDK_INSTALL_DIR" ]; then
        echo -e "${RED}错误：JDK 已经安装，请先卸载已有的 JDK。${NC}"
        exit 1
    fi

    # 获取 JDK 压缩包文件名
    JDK_ARCHIVE="$1"
    # 检查 JDK 压缩包是否存在
    if [ ! -f "$JDK_ARCHIVE" ]; then
        echo -e "${RED}错误：找不到指定的 JDK 压缩包，请确保路径和文件名正确。${NC}"
        exit 1
    fi

    # 解压 JDK 到安装目录
    sudo mkdir -p $JDK_INSTALL_DIR
    sudo tar -zxvf $JDK_ARCHIVE -C $JDK_INSTALL_DIR --strip-components=1

    # 检查解压是否成功
    if [ $? -ne 0 ]; then
        echo -e "${RED}错误：解压 JDK 失败。${NC}"
        exit 1
    fi

    # 设置环境变量
    echo "export JAVA_HOME=$JDK_INSTALL_DIR" | sudo tee -a /etc/profile
    echo 'export PATH=$JAVA_HOME/bin:$PATH' | sudo tee -a /etc/profile

    # 刷新环境变量
    source /etc/profile

    # 获取安装的 JDK 版本
    JDK_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    # 显示安装信息
    echo -e "${GREEN}JDK $JDK_VERSION 已安装到 $JDK_INSTALL_DIR。${NC}"
    echo -e "${GREEN}run java project com as follow:${NC}"
    echo -e "${GREEN}nohup java -jar springboot-xxx.jar > springboot-xxx.log &${NC}"
}

# 卸载 JDK
uninstall_jdk() {
    # 检查是否已安装 JDK
    if [ ! -d "$JDK_INSTALL_DIR" ]; then
        echo -e "${RED}错误：未找到已安装的 JDK。${NC}"
        exit 1
    fi

    # 删除 JDK 安装目录
    sudo rm -rf $JDK_INSTALL_DIR

    # 移除环境变量
    sudo sed -i '/export JAVA_HOME=\/usr\/local\/java/d' /etc/profile
    sudo sed -i '/export PATH=\$JAVA_HOME\/bin:\$PATH/d' /etc/profile

    # 刷新环境变量
    source /etc/profile

    echo -e "${GREEN}JDK 已成功卸载。${NC}"
}

# 在线安装 JDK
install_online() {
    echo -e "${GREEN}正在在线安装 JDK...${NC}"
    # 使用 apt-get（Debian/Ubuntu）
    if command -v apt-get &>/dev/null; then
        sudo apt-get update
        sudo apt-get install -y default-jdk
    # 使用 yum（CentOS/RHEL）
    elif command -v yum &>/dev/null; then
        sudo yum install -y java
    # 使用 zypper（openSUSE）
    elif command -v zypper &>/dev/null; then
        sudo zypper install -y java-11-openjdk
    else
        echo -e "${RED}错误：未找到适用的包管理器。${NC}"
        exit 1
    fi
}

# 执行用户选择的操作
case "$1" in
    install)
        shift
        install_jdk "$@"
        ;;
    uninstall)
        uninstall_jdk
        ;;
    online)
        if check_internet; then
            install_online
        else
            echo -e "${RED}错误：未检测到网络连接，请检查网络并重试。${NC}"
            exit 1
        fi
        ;;
    *)
        echo -e "${RED}错误：未提供正确的操作参数。${NC}"
        echo -e "用法：$0 <${GREEN}install | uninstall | online${NC}> [jdk_archive_filename]"
        exit 1
        ;;
esac
