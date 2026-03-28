#!/bin/bash

# WMS 部署脚本
# 用法: ./deploy.sh [环境]
# 示例: ./deploy.sh prod

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 环境配置
ENV=${1:-dev}
COMPOSE_FILE="deploy/docker/docker-compose.yml"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  WMS 仓库管理系统部署脚本${NC}"
echo -e "${GREEN}  环境: $ENV${NC}"
echo -e "${GREEN}========================================${NC}"

# 检查Docker和Docker Compose
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker未安装${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}错误: Docker Compose未安装${NC}"
    exit 1
fi

# 创建必要目录
echo -e "${YELLOW}[1/5] 创建必要目录...${NC}"
mkdir -p data/mysql
mkdir -p data/redis
mkdir -p logs/backend
mkdir -p logs/nginx

# 检查配置文件
echo -e "${YELLOW}[2/5] 检查配置文件...${NC}"
if [ ! -f "$COMPOSE_FILE" ]; then
    echo -e "${RED}错误: 未找到docker-compose.yml文件${NC}"
    exit 1
fi

# 拉取最新镜像
echo -e "${YELLOW}[3/5] 拉取最新镜像...${NC}"
docker-compose -f $COMPOSE_FILE pull

# 停止旧服务
echo -e "${YELLOW}[4/5] 停止旧服务...${NC}"
docker-compose -f $COMPOSE_FILE down

# 启动服务
echo -e "${YELLOW}[5/5] 启动服务...${NC}"
docker-compose -f $COMPOSE_FILE up -d

# 等待服务启动
echo -e "${YELLOW}等待服务启动...${NC}"
sleep 10

# 健康检查
echo -e "${YELLOW}执行健康检查...${NC}"
HEALTH_STATUS=$(docker-compose -f $COMPOSE_FILE ps | grep -c "Up")
if [ $HEALTH_STATUS -ge 4 ]; then
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}  部署成功！${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}  前端访问: http://localhost${NC}"
    echo -e "${GREEN}  后端API: http://localhost:8080/api${NC}"
    echo -e "${GREEN}========================================${NC}"
else
    echo -e "${RED}警告: 部分服务可能未正常启动${NC}"
    docker-compose -f $COMPOSE_FILE ps
fi

# 显示日志
echo -e "${YELLOW}显示最近日志...${NC}"
docker-compose -f $COMPOSE_FILE logs --tail=20
