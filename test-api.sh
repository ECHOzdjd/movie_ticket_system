#!/bin/bash

echo "================================"
echo "电影票系统 API 测试"
echo "================================"
echo ""

echo "1. 测试影片列表接口"
echo "GET http://localhost:8081/movie/list"
curl -s http://localhost:8081/movie/list | python3 -m json.tool | head -30
echo ""
echo ""

echo "2. 测试影片详情接口"
echo "GET http://localhost:8081/movie/1"
curl -s http://localhost:8081/movie/1 | python3 -m json.tool
echo ""
echo ""

echo "3. 测试影院列表接口"
echo "GET http://localhost:8081/cinema/list"
curl -s http://localhost:8081/cinema/list | python3 -m json.tool | head -30
echo ""
echo ""

echo "4. 测试场次列表接口"
echo "GET http://localhost:8081/schedule/movie/1"
curl -s http://localhost:8081/schedule/movie/1 | python3 -m json.tool | head -30
echo ""
echo ""

echo "5. 测试用户注册接口"
echo "POST http://localhost:8082/user/register"
curl -s -X POST http://localhost:8082/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","phone":"13800138000","email":"test@example.com"}' | python3 -m json.tool
echo ""
echo ""

echo "6. 测试用户登录接口"
echo "POST http://localhost:8082/user/login"
LOGIN_RESULT=$(curl -s -X POST http://localhost:8082/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}')
echo "$LOGIN_RESULT" | python3 -m json.tool
echo ""

# 从登录结果中提取token
TOKEN=$(echo "$LOGIN_RESULT" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('data', {}).get('token', ''))" 2>/dev/null)

if [ -n "$TOKEN" ]; then
    echo "获取到Token: ${TOKEN:0:20}..."
    echo ""
    echo ""
    
    echo "7. 测试获取用户信息接口（需要Token）"
    echo "GET http://localhost:8082/user/info"
    curl -s http://localhost:8082/user/info \
      -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
    echo ""
    echo ""
    
    echo "8. 测试查询座位图接口（需要Token）"
    echo "GET http://localhost:8082/order/seats/1"
    curl -s http://localhost:8082/order/seats/1 \
      -H "Authorization: Bearer $TOKEN" | python3 -m json.tool | head -30
    echo ""
fi

echo ""
echo "================================"
echo "测试完成！"
echo "================================"
