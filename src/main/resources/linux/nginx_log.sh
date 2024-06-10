#!/bin/bash

# 设置颜色代码
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# 开启 logrotate 任务
start_logrotate() {
    # 创建 logrotate 配置文件
    echo "/path/to/nginx/logs/*.log {
    daily
    rotate 7
    missingok
    notifempty
    compress
    delaycompress
    sharedscripts
    postrotate
        systemctl reload nginx
    endscript
}" | sudo tee /etc/logrotate.d/nginx >/dev/null

    # 每天凌晨执行 logrotate
    echo "@daily /usr/sbin/logrotate -f /etc/logrotate.d/nginx" | sudo tee -a /etc/crontab >/dev/null
    sudo systemctl restart cron
    echo -e "${GREEN}logrotate 任务已启动。${NC}"
}

# 停止 logrotate 任务
stop_logrotate() {
    sudo rm /etc/logrotate.d/nginx
    sudo sed -i '/logrotate/d' /etc/crontab
    sudo systemctl restart cron
    echo -e "${GREEN}logrotate 任务已停止。${NC}"
}

# 查看 logrotate 任务状态
status_logrotate() {
    if [ -e /etc/logrotate.d/nginx ]; then
        echo -e "${GREEN}logrotate 任务已启动。${NC}"
    else
        echo -e "${RED}logrotate 任务未启动。${NC}"
    fi
}

# 主函数
main() {
    if [ "$#" -ne 1 ]; then
        echo "用法: $0 <start | stop | status>"
        exit 1
    fi

    case "$1" in
        start)
            start_logrotate
            ;;
        stop)
            stop_logrotate
            ;;
        status)
            status_logrotate
            ;;
        *)
            echo "未知的参数: $1"
            echo "用法: $0 <start | stop | status>"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
