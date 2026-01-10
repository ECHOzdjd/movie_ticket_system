#!/bin/bash

echo "========================================="
echo "   电影票务系统 - API 测试脚本"
echo "========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
GATEWAY_URL="http://localhost:9999/api"
MOVIE_SERVICE_URL="http://localhost:8081"
ORDER_SERVICE_URL="http://localhost:8082"

# 测试计数
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 测试函数
test_api() {
    local test_name=$1
    local command=$2
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    echo -e "${BLUE}[测试 $TOTAL_TESTS]${NC} $test_name"
    
    result=$(eval $command 2>&1)
    exit_code=$?
    
    if [ $exit_code -eq 0 ] && echo "$result" | grep -q '"code":200'; then
        echo -e "${GREEN}  ✓ 通过${NC}"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        echo "$result" | python3 -m json.tool 2>/dev/null | head -15 | sed 's/^/    /'
    else
        echo -e "${RED}  ✗ 失败${NC}"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        echo "$result" | sed 's/^/    /'
    fi
    echo ""
}

# 检查服务状态
echo -e "${YELLOW}=== 检查服务状态 ===${NC}"
check_service() {
    local name=$1
    local url=$2
    if curl -s "$url" > /dev/null 2>&1; then
        echo -e "  ${GREEN}✓${NC} $name"
    else
        echo -e "  ${RED}✗${NC} $name (无法连接)"
    fi
}

check_service "Gateway Service (9999)" "http://localhost:9999"
check_service "Movie Service (8081)" "$MOVIE_SERVICE_URL"
check_service "Order Service (8082)" "$ORDER_SERVICE_URL"
echo ""

# 生成随机用户名
RANDOM_USER="user_$(date +%s)"

# 用户相关API测试
echo -e "${YELLOW}=========================================${NC}"
echo -e "${YELLOW}1. 用户服务测试${NC}"
echo -e "${YELLOW}=========================================${NC}"
echo ""

test_api "用户注册" \
    "curl -s -X POST $GATEWAY_URL/user/register \
    -H 'Content-Type: application/json' \
    -d '{\"username\":\"$RANDOM_USER\",\"password\":\"123456\",\"phone\":\"13800138000\",\"email\":\"test@example.com\"}'"

test_api "用户登录" \
    "curl -s -X POST $GATEWAY_URL/user/login \
    -H 'Content-Type: application/json' \
    -d '{\"username\":\"$RANDOM_USER\",\"password\":\"123456\"}'"

# 获取token和userId
LOGIN_RESULT=$(curl -s -X POST $GATEWAY_URL/user/login \
    -H 'Content-Type: application/json' \
    -d "{\"username\":\"$RANDOM_USER\",\"password\":\"123456\"}")
TOKEN=$(echo "$LOGIN_RESULT" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('token', ''))" 2>/dev/null)
USER_ID=$(echo "$LOGIN_RESULT" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('userId', ''))" 2>/dev/null)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}  ✗ 无法获取登录Token，后续测试将失败${NC}"
    echo ""
else
    echo -e "${GREEN}  ✓ 获取Token成功: ${TOKEN:0:30}...${NC}"
    echo ""
fi

test_api "获取用户信息" \
    "curl -s $GATEWAY_URL/user/info -H 'Authorization: Bearer $TOKEN'"

# 电影资源服务测试
echo -e "${YELLOW}=========================================${NC}"
echo -e "${YELLOW}2. 电影资源服务测试${NC}"
echo -e "${YELLOW}=========================================${NC}"
echo ""

test_api "查询电影列表" \
    "curl -s $GATEWAY_URL/movie/list"

test_api "查询电影详情 (ID=1)" \
    "curl -s $GATEWAY_URL/movie/1"

test_api "查询影院列表" \
    "curl -s $GATEWAY_URL/cinema/list"

test_api "查询影院详情 (ID=1)" \
    "curl -s $GATEWAY_URL/cinema/1"

test_api "查询电影排期 (电影ID=1)" \
    "curl -s $GATEWAY_URL/schedule/movie/1"

test_api "查询排期详情 (排期ID=1)" \
    "curl -s $GATEWAY_URL/schedule/1"

# 订单相关API测试
echo -e "${YELLOW}=========================================${NC}"
echo -e "${YELLOW}3. 订单服务测试${NC}"
echo -e "${YELLOW}=========================================${NC}"
echo ""

test_api "查询排期座位图 (排期ID=1)" \
    "curl -s $GATEWAY_URL/seat/map/1 -H 'Authorization: Bearer $TOKEN'"

# 生成随机座位号避免冲突
RANDOM_SEAT="Z$((RANDOM % 10 + 1))"

test_api "锁定座位" \
    "curl -s -X POST $GATEWAY_URL/seat/lock \
    -H 'Content-Type: application/json' \
    -H 'Authorization: Bearer $TOKEN' \
    -d '{\"scheduleId\":1,\"seatNumbers\":[\"$RANDOM_SEAT\"]}'"

test_api "创建订单" \
    "curl -s -X POST $GATEWAY_URL/order/create \
    -H 'Content-Type: application/json' \
    -H 'Authorization: Bearer $TOKEN' \
    -d '{\"scheduleId\":1,\"seatNumbers\":[\"Y$((RANDOM % 10 + 1))\"]}'"

# 统计结果
echo ""
echo -e "${YELLOW}=========================================${NC}"
echo -e "${YELLOW}         测试结果统计${NC}"
echo -e "${YELLOW}=========================================${NC}"
echo ""
echo "  总测试数: $TOTAL_TESTS"
echo -e "  ${GREEN}✓ 通过: $PASSED_TESTS${NC}"
echo -e "  ${RED}✗ 失败: $FAILED_TESTS${NC}"
echo ""

SUCCESS_RATE=$((PASSED_TESTS * 100 / TOTAL_TESTS))
echo "  成功率: ${SUCCESS_RATE}%"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}🎉 所有测试通过！${NC}"
    exit 0
else
    echo -e "${RED}⚠️  有 $FAILED_TESTS 个测试失败，请检查日志${NC}"
    exit 1
fi

    echo -e "${RED}部分测试失败，请检查日志${NC}"
    exit 1
fi
