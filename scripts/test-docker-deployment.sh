#!/bin/bash

echo "========================================="
echo "   Docker Compose 部署测试"
echo "========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 1. 检查 Docker 镜像
echo -e "${YELLOW}=== 1. 检查 Docker 镜像 ===${NC}"
docker images | grep "movie-ticket-system"
echo ""

# 2. 启动所有服务
echo -e "${YELLOW}=== 2. 启动所有服务（包括微服务容器）===${NC}"
echo "停止现有的 Java 进程..."
pkill -f "movie-resource-service-1.0.0.jar" 2>/dev/null
pkill -f "order-biz-service-1.0.0.jar" 2>/dev/null
pkill -f "gateway-service-1.0.0.jar" 2>/dev/null
sleep 2

echo "启动 Docker Compose 服务..."
docker compose up -d
echo ""

# 3. 等待服务启动
echo -e "${YELLOW}=== 3. 等待服务启动 ===${NC}"
echo "等待 30 秒让所有服务完全启动..."
for i in {30..1}; do
    echo -ne "  倒计时: $i 秒\r"
    sleep 1
done
echo ""
echo ""

# 4. 检查容器状态
echo -e "${YELLOW}=== 4. 检查容器状态 ===${NC}"
docker compose ps
echo ""

# 5. 检查服务日志
echo -e "${YELLOW}=== 5. 检查微服务启动日志 ===${NC}"
echo -e "${BLUE}--- Movie Resource Service ---${NC}"
docker compose logs --tail=5 movie-resource-service
echo ""
echo -e "${BLUE}--- Order Biz Service ---${NC}"
docker compose logs --tail=5 order-biz-service
echo ""
echo -e "${BLUE}--- Gateway Service ---${NC}"
docker compose logs --tail=5 gateway-service
echo ""

# 6. 测试 API
echo -e "${YELLOW}=== 6. 测试 API 连通性 ===${NC}"

test_api() {
    local name=$1
    local url=$2
    local expected=$3
    
    echo -ne "  测试 $name ... "
    response=$(curl -s "$url" 2>/dev/null)
    
    if echo "$response" | grep -q "$expected"; then
        echo -e "${GREEN}✓ 通过${NC}"
    else
        echo -e "${RED}✗ 失败${NC}"
        echo "    响应: $response"
    fi
}

test_api "Gateway 健康检查" "http://localhost:9999/api/movie/list" '"code":200'
test_api "Movie Service" "http://localhost:8081/movie/list" '"code":200'
test_api "Order Service 注册" "http://localhost:8082/user/register" '"code":'

echo ""

# 7. 完整 API 测试
echo -e "${YELLOW}=== 7. 运行完整 API 测试 ===${NC}"
sleep 5
bash test-all-apis.sh

echo ""
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}   Docker Compose 部署测试完成${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo "查看服务状态: docker compose ps"
echo "查看服务日志: docker compose logs [service-name]"
echo "停止所有服务: docker compose down"
echo ""
