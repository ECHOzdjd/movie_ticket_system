# 在线电影票购票与选座系统

基于 Spring Cloud 的分布式微服务电影票购票与选座系统。

## 项目介绍

### 项目背景

随着互联网的发展，在线购票已成为主流的电影票购买方式。本系统旨在构建一个高可用、高并发的分布式电影票购票系统，提供完善的影片管理、场次查询、在线选座、订单管理等功能。

### 核心功能

#### 1. 影片与资源管理（movie-resource-service）
- 查询上映影片列表
- 查看影片详情（包括演员、评分、简介等）
- 查询影院信息
- 查询场次排期（Schedule）

#### 2. 订单与用户管理（order-biz-service）
- 用户注册、登录（BCrypt加密）
- 个人信息管理
- 查询场次的实时座位图
- 在线选座、分布式锁座
- 订单创建与状态管理（待支付/已完成/已取消）
- 自动取消超时订单

#### 3. 统一网关与服务治理（gateway-service）
- API 网关统一入口（端口: 9999）
- 路由转发与负载均衡
- CORS跨域支持
- 服务自动注册与发现

### 技术栈

#### 开发框架与服务治理
- **微服务框架**: Spring Boot 3.1.5, Spring Cloud 2022.0.4
- **注册中心**: Nacos 2.2.3
- **配置中心**: Nacos Config
- **网关**: Spring Cloud Gateway (WebFlux)
- **负载均衡**: Spring Cloud LoadBalancer
- **服务调用**: OpenFeign

#### 数据存储

- **关系型数据库**: MySQL 8.0
- **ORM框架**: MyBatis Plus 3.5.5
- **缓存**: Redis 7
- **对象映射**: Jackson

#### 数据存储
- **数据库**: MySQL 8.0 + MyBatis-Plus 3.5.5
- **缓存**: Redis 7 (分布式锁、缓存)

#### 其他技术
- **认证**: JWT (JSON Web Token)
- **密码加密**: BCrypt
- **序列化**: Jackson (含 JavaTimeModule)
- **服务调用**: Spring Cloud OpenFeign
- **容器化**: Docker, Docker Compose
- **构建工具**: Maven
- **JDK版本**: Java 17

## 系统架构

### 架构图

```
                    ┌─────────────┐
                    │   Client    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │   Gateway   │
                    │   Service   │
                    │   :9999     │
                    └──┬──────┬───┘
                       │      │
        ┌──────────────┘      └──────────────┐
        │                                     │
┌───────▼────────┐                 ┌─────────▼───────┐
│ Movie Resource │                 │  Order Biz      │
│    Service     │                 │    Service      │
│    :8081       │◄────────────────┤    :8082        │
└───────┬────────┘   OpenFeign     └────────┬────────┘
        │                                    │
        │            ┌───────────────────────┤
        │            │                       │
┌───────▼────────────▼───┐         ┌────────▼────────┐
│       MySQL 8.0        │         │     Redis 7     │
│        :3306           │         │      :6379      │
└────────────────────────┘         └─────────────────┘
        │
        │
┌───────▼────────┐
│  Nacos 2.2.3   │
│  Registry &    │
│     Config     │
│   :8848        │
└────────────────┘
```

### 服务说明

| 服务名称 | 端口 | 状态 | 职责说明 |
|---------|------|------|---------|
| gateway-service | 9999 | ✅ | API网关，统一入口，路由转发，CORS |
| movie-resource-service | 8081 | ✅ | 影片资源服务，管理影片、影院、场次 |
| order-biz-service | 8082 | ✅ | 订单业务服务，管理用户、选座、订单 |
| Nacos Server | 8848 | ✅ | 服务注册与发现、配置管理 |
| MySQL | 3306 | ✅ | 数据存储 |
| Redis | 6379 | ✅ | 缓存、分布式锁 |

## 快速开始

### 环境要求

- **JDK**: 17 或更高版本
- **Maven**: 3.6+
- **Docker & Docker Compose**: 推荐使用

### 方式一：Docker Compose 一键部署（推荐⭐）

使用 Docker Compose 可以一键启动所有服务,包括基础设施和微服务。

```bash
# 1. 编译项目
mvn clean package -DskipTests

# 2. 启动所有服务（包括 MySQL、Redis、Nacos 和所有微服务）
docker compose up -d

# 3. 等待服务启动（约 30 秒）
sleep 30

# 4. 验证服务状态
docker compose ps

# 5. 查看服务日志
docker compose logs -f gateway-service

# 6. 测试 API
bash test-all-apis.sh
```

**管理命令：**

```bash
# 停止所有服务
docker compose down

# 重启某个服务
docker compose restart gateway-service

# 查看实时日志
docker compose logs -f [service-name]

# 删除所有容器和数据卷
docker compose down -v
```

### 方式二：本地运行微服务

如果只想容器化基础设施,微服务在本地运行:

1. **启动基础设施**

```bash
# 启动 MySQL, Redis, Nacos
docker compose up -d mysql redis nacos
# 等待 15 秒让 Nacos 完全启动
sleep 15
```

2. **编译并启动微服务**

```bash
# 编译项目
mvn clean package -DskipTests

# 创建日志目录
mkdir -p logs

# 启动所有服务
nohup java -jar movie-resource-service/target/movie-resource-service-1.0.0.jar > logs/movie-resource-service.log 2>&1 &
nohup java -jar order-biz-service/target/order-biz-service-1.0.0.jar > logs/order-biz-service.log 2>&1 &
nohup java -jar gateway-service/target/gateway-service-1.0.0.jar > logs/gateway-service.log 2>&1 &
```

3. **验证服务**

```bash
# 访问 Nacos 控制台（默认账号/密码: nacos/nacos）
# http://localhost:8848/nacos

# 测试 API
curl http://localhost:9999/api/movie/list
```

### 测试账号

系统已预置测试账号：

| 用户名 | 密码 | 说明 |
|--------|------|------|
| admin | 123456 | 管理员账号 |
| testuser | 123456 | 普通用户 |

### 快速测试

```bash
# 运行完整测试脚本
bash test-all-apis.sh
```

## API文档

### 基础路径

- **网关统一入口**: `http://localhost:9999/api`
- **直接访问服务**: 
  - 电影服务: `http://localhost:8081`
  - 订单服务: `http://localhost:8082`

### 认证说明

系统使用 **JWT (JSON Web Token)** 进行身份认证。部分 API 需要在请求头中携带有效的 Token。

#### 认证流程

1. **登录获取 Token**
   ```bash
   curl -X POST http://localhost:9999/api/user/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"123456"}'
   
   # 返回示例
   {
     "code": 200,
     "message": "操作成功",
     "data": {
       "token": "eyJhbGciOiJIUzUxMiJ9..."
     }
   }
   ```

2. **携带 Token 访问受保护的 API**
   ```bash
   curl http://localhost:9999/api/user/info \
     -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
   ```

#### API 认证要求

| API 类型 | 是否需要 Token | 说明 |
|---------|---------------|------|
| 用户注册 `/api/user/register` | ❌ | 公开接口 |
| 用户登录 `/api/user/login` | ❌ | 公开接口 |
| 用户信息 `/api/user/info` | ✅ | 需要 Token |
| 电影查询 `/api/movie/**` | ❌ | 公开接口 |
| 影院查询 `/api/cinema/**` | ❌ | 公开接口 |
| 排期查询 `/api/schedule/**` | ❌ | 公开接口 |
| 座位操作 `/api/seat/**` | ✅ | 需要 Token |
| 订单操作 `/api/order/**` | ✅ | 需要 Token |

### 主要接口

#### 1. 用户服务

```bash
# 用户注册（公开接口）
POST http://localhost:9999/api/user/register
Content-Type: application/json
{
  "username": "newuser",
  "password": "123456",
  "phone": "13800138000",
  "email": "user@example.com"
}

# 用户登录（公开接口）
POST http://localhost:9999/api/user/login
Content-Type: application/json
{
  "username": "admin",
  "password": "123456"
}
# 返回: {"code":200,"data":{"token":"eyJ..."}}

# 获取用户信息（需要 Token）
GET http://localhost:9999/api/user/info
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

#### 2. 电影服务（公开接口）

```bash
# 查询电影列表
GET http://localhost:9999/api/movie/list

# 查询电影详情
GET http://localhost:9999/api/movie/{id}

# 查询影院列表
GET http://localhost:9999/api/cinema/list

# 查询电影排期
GET http://localhost:9999/api/schedule/movie/{movieId}
```

#### 3. 订单服务（需要 Token）

```bash
# 查询座位图（需要 Token）
GET http://localhost:9999/api/seat/map/{scheduleId}
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...

# 锁定座位（需要 Token）
POST http://localhost:9999/api/seat/lock
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json
{
  "scheduleId": 1,
  "seatNumbers": ["A1", "A2"]
}

# 创建订单（需要 Token）
POST http://localhost:9999/api/order/create
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json
{
  "scheduleId": 1,
  "seatNumbers": ["A1", "A2"]
}
```

详细 API 文档请查看 [docs/api.md](docs/api.md)

## Nacos 配置管理

### 访问控制台

- URL: http://localhost:8848/nacos
- 账号: nacos
- 密码: nacos

### 命名空间管理

系统支持多环境隔离，当前配置为 **dev（开发环境）**。

创建其他命名空间：
1. 登录 Nacos 控制台
2. 左侧菜单 → 命名空间
3. 点击"新建命名空间"
4. 填写命名空间ID和名称（如 test、prod）

修改服务命名空间：
```yaml
# application.yml
spring:
  cloud:
    nacos:
      discovery:
        namespace: ${NACOS_NAMESPACE:dev}  # 修改此处
```

### 配置热更新

Nacos 支持配置热更新，无需重启服务：
1. 在配置管理中创建配置文件
2. 服务通过 `@RefreshScope` 注解实现配置动态刷新

## 项目结构

```
movie-ticket-system/
├── common/                      # 公共模块
│   └── src/main/java/com/movie/common/
│       ├── constant/           # 常量定义
│       ├── dto/                # 数据传输对象
│       ├── exception/          # 异常类
│       ├── result/             # 统一响应结果
│       └── util/               # 工具类
├── gateway-service/            # API 网关服务 (9999)
│   ├── src/main/java/com/movie/gateway/
│   │   ├── config/             # 配置类（CORS等）
│   │   └── filter/             # 网关过滤器
│   └── src/main/resources/
│       └── application.yml
├── movie-resource-service/     # 影片资源服务 (8081)
│   ├── src/main/java/com/movie/movie/
│   │   ├── controller/         # REST API 控制器
│   │   ├── entity/             # 实体类
│   │   ├── mapper/             # MyBatis Mapper
│   │   └── service/            # 业务逻辑层
│   └── src/main/resources/
│       └── application.yml
├── order-biz-service/          # 订单业务服务 (8082)
│   ├── src/main/java/com/movie/order/
│   │   ├── config/             # 配置类（Jackson等）
│   │   ├── controller/         # REST API 控制器
│   │   ├── entity/             # 实体类
│   │   ├── feign/              # Feign 客户端
│   │   ├── mapper/             # MyBatis Mapper
│   │   └── service/            # 业务逻辑层
│   └── src/main/resources/
│       └── application.yml
├── sql/                        # 数据库脚本
│   ├── schema.sql             # 表结构
│   └── init_data.sql          # 初始数据
├── docs/                       # 项目文档
│   ├── api.md                 # API 详细文档
│   ├── architecture.md        # 架构设计
│   └── deployment.md          # 部署指南
├── docker-compose.yml         # Docker Compose 配置
├── test-all-apis.sh           # API 测试脚本
└── pom.xml                    # Maven 父 POM
```

## 常见问题

### 1. 端口冲突

如果遇到端口被占用，可以修改 `application.yml` 中的端口配置：

```yaml
server:
  port: 9999  # 修改为其他可用端口
```

### 2. Nacos 连接失败

确保 Nacos 已完全启动（大约需要 15 秒）：

```bash
docker logs movie-ticket-nacos
```

### 3. 数据库连接失败

检查 MySQL 容器状态和配置：

```bash
docker ps | grep mysql
docker logs movie-ticket-mysql
```

### 4. 服务未注册到 Nacos

1. 检查服务日志中的错误信息
2. 确认 Nacos 地址配置正确
3. 验证命名空间配置（当前为 dev）

## 技术亮点

1. **服务隔离**: 使用 Nacos 命名空间实现多环境隔离（dev/test/prod）
2. **配置管理**: 支持配置集中管理和热更新
3. **容错机制**: Feign 调用支持超时和重试
4. **安全加密**: BCrypt 密码加密，JWT Token 认证
5. **序列化优化**: Jackson 配置 JavaTimeModule 处理日期时间
6. **响应式网关**: Spring Cloud Gateway 基于 WebFlux 实现高性能路由

## 开发团队

- 架构设计与实现
- 持续优化中...

## 许可证

本项目仅供学习和研究使用。

---

**文档最后更新**: 2025年12月28日
│   ├── src/main/java/com/movie/order/
│   │   ├── controller/         # 控制器
│   │   ├── entity/             # 实体类
│   │   ├── feign/              # Feign客户端
│   │   ├── mapper/             # Mapper
│   │   ├── service/            # 服务层
│   │   └── config/             # 配置类
│   ├── Dockerfile
│   └── pom.xml
├── sql/                        # SQL脚本
│   ├── schema.sql              # 数据库表结构
│   └── init_data.sql           # 初始化数据
├── docs/                       # 文档目录
│   ├── api.md                  # API文档
│   ├── architecture.md         # 架构说明
│   └── deployment.md           # 部署说明
├── docker-compose.yml          # Docker Compose配置
├── pom.xml                     # 父POM
└── README.md                   # 项目说明
```

## API 文档

详细API文档请参考 [docs/api.md](docs/api.md)

## 架构文档

详细架构说明请参考 [docs/architecture.md](docs/architecture.md)

## 部署文档

详细部署说明请参考 [docs/deployment.md](docs/deployment.md)

## 系统特色

1. **精简微服务架构**: 基于 Spring Boot 3.2.0 + Spring Cloud 2023.0.0 + Nacos 构建，减少维护成本
2. **高效在线选座**: 采用分布式锁防止座位重复选择，确保高并发下的数据一致性
3. **实时状态同步**: 支持场次座位状态的实时查询与同步
4. **完善的服务治理**: 支持负载均衡、服务注册发现等治理特性
5. **高内聚低耦合**: 模块划分清晰，核心业务通过 OpenFeign 实现服务间高可读性调用
6. **统一异常处理**: 全局异常处理器，统一响应格式
7. **JWT认证**: 基于Token的无状态认证机制
8. **自动化部署**: Docker容器化，支持Docker Compose一键部署

## 开发规范

1. **代码规范**: 遵循阿里巴巴Java开发手册
2. **接口规范**: RESTful API设计
3. **数据库规范**: 表名小写，字段下划线命名
4. **日志规范**: 使用SLF4J + Logback
5. **异常处理**: 统一使用BusinessException

## 常见问题

### Q1: 服务拆分粒度如何把握？

A: 初期不要拆分太细，按业务领域拆分，3-5个服务为宜，保证每个服务职责清晰。

### Q2: 是否必须使用消息队列？

A: 不是必须的，如果有异步场景可以使用，可以作为加分项。

### Q3: 如何处理分布式事务？

A: 优先考虑避免分布式事务，可以使用本地消息表或使用Saga补偿模式，保证最终一致性即可。

### Q4: 是否需要前端？

A: 不需要，本项目专注于后端微服务实现。

## 贡献指南

欢迎提交Issue和Pull Request！

## 许可证

MIT License

## 联系方式

如有问题，请联系项目维护者。

