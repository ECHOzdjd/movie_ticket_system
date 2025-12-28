# 快速启动指南

## 方式一：Docker Compose 完整部署（推荐）

```bash
# 1. 编译项目
mvn clean package -DskipTests

# 2. 启动所有服务（包括基础设施和微服务）
docker compose up -d

# 3. 等待服务启动
sleep 30

# 4. 验证服务
docker compose ps

# 5. 测试 API
curl http://localhost:9999/api/movie/list
```

## 方式二：本地运行微服务

```bash
# 1. 启动基础设施（MySQL, Redis, Nacos）
docker compose up -d mysql redis nacos
sleep 15  # 等待 Nacos 启动

# 2. 编译项目
mvn clean package -DskipTests

# 3. 启动所有微服务
mkdir -p logs
nohup java -jar movie-resource-service/target/movie-resource-service-1.0.0.jar > logs/movie-resource.log 2>&1 &
nohup java -jar order-biz-service/target/order-biz-service-1.0.0.jar > logs/order-biz.log 2>&1 &
nohup java -jar gateway-service/target/gateway-service-1.0.0.jar > logs/gateway.log 2>&1 &

# 4. 测试服务
sleep 20
curl http://localhost:9999/api/movie/list
```

## 服务端口

| 服务 | 端口 | 地址 |
|------|------|------|
| Gateway | 9999 | http://localhost:9999 |
| Movie Service | 8081 | http://localhost:8081 |
| Order Service | 8082 | http://localhost:8082 |
| Nacos Console | 8848 | http://localhost:8848/nacos |
| MySQL | 3306 | localhost:3306 |
| Redis | 6379 | localhost:6379 |

## 测试账号

| 用户名 | 密码 | 说明 |
|--------|------|------|
| admin | 123456 | 管理员 |
| testuser | 123456 | 普通用户 |

## 常用命令

### 查看服务状态

```bash
# 查看进程
ps aux | grep "movie-resource\|order-biz\|gateway" | grep -v grep

# 查看日志
tail -f logs/gateway.log
tail -f logs/movie-resource.log
tail -f logs/order-biz.log

# 查看 Docker 容器
docker ps
```

### 停止服务

```bash
# Docker Compose 方式：停止所有服务
docker compose down

# 本地运行方式：停止微服务
pkill -f "movie-resource-service-1.0.0.jar"
pkill -f "order-biz-service-1.0.0.jar"
pkill -f "gateway-service-1.0.0.jar"

# 停止基础设施容器
docker compose down mysql redis nacos
```

### 重新启动

```bash
# 重启单个服务（以 gateway 为例）
pkill -f "gateway-service-1.0.0.jar"
nohup java -jar gateway-service/target/gateway-service-1.0.0.jar > logs/gateway.log 2>&1 &
```

## 快速测试

### 1. 测试电影服务

```bash
# 电影列表
curl http://localhost:9999/api/movie/list

# 电影详情
curl http://localhost:9999/api/movie/1

# 影院列表
curl http://localhost:9999/api/cinema/list
```

### 2. 测试用户服务

```bash
# 用户注册
curl -X POST http://localhost:9999/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser2",
    "password": "123456",
    "phone": "13900139000",
    "email": "test2@example.com"
  }'

# 用户登录（获取 Token）
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:9999/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }')
echo $LOGIN_RESPONSE

# 提取 Token
TOKEN=$(echo $LOGIN_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['token'])")

# 获取用户信息（需要 Token）
curl http://localhost:9999/api/user/info \
  -H "Authorization: Bearer $TOKEN"
```

### 3. 运行完整测试

```bash
bash test-all-apis.sh
```

## Nacos 管理

### 访问控制台

```
URL: http://localhost:8848/nacos
账号: nacos
密码: nacos
```

### 查看服务列表

```bash
curl "http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=10&namespaceId=dev"
```

### 查看服务实例

```bash
curl "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=gateway-service&namespaceId=dev"
```

## 故障排查

### 服务无法启动

1. 检查端口是否被占用：`lsof -i :9999`
2. 查看日志：`tail -100 logs/gateway.log`
3. 检查 Nacos 是否启动：`docker logs movie-ticket-nacos`

### 服务未注册到 Nacos

1. 确认命名空间配置正确（当前：dev）
2. 检查 Nacos 地址：`localhost:8848`
3. 查看服务日志中的注册信息

### 数据库连接失败

1. 检查 MySQL 容器：`docker ps | grep mysql`
2. 测试连接：`docker exec movie-ticket-mysql mysql -uroot -proot -e "SELECT 1"`
3. 查看表：`docker exec movie-ticket-mysql mysql -uroot -proot movie_ticket -e "SHOW TABLES"`

## 更多文档

- [完整 README](README.md)
- [API 文档](docs/api.md)
- [架构设计](docs/architecture.md)
- [部署指南](docs/deployment.md)

---

**快速参考** | 最后更新: 2025-12-28
