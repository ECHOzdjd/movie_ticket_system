# 部署文档

本文档说明如何部署电影票购票系统。

## 部署方式

系统支持两种部署方式：

1. **Docker Compose部署**（推荐）：一键启动所有服务
2. **本地开发部署**：适用于开发和调试

## 方式一：Docker Compose部署（推荐）

### 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- Maven 3.6+（用于构建项目）

### 部署步骤

#### 1. 克隆项目

```bash
git clone <repository-url>
cd movie-ticket-system
```

#### 2. 构建项目

```bash
# 编译打包所有服务
mvn clean package -DskipTests
```

#### 3. 启动服务

```bash
# 启动所有服务
docker compose up -d

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f
```

#### 4. 验证部署

- **Nacos控制台**: http://localhost:8848/nacos
  - 默认账号/密码: `nacos/nacos`
  - 检查服务注册情况

- **网关服务**: http://localhost:9999
  - 测试接口: `GET http://localhost:9999/api/movie/list`

- **影片服务**: http://localhost:8081
- **订单服务**: http://localhost:8082

#### 5. 停止服务

```bash
# 停止所有服务
docker compose down

# 停止并删除数据卷
docker compose down -v
```

## 方式二：本地开发部署

### 前置要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7+
- Nacos 2.2.3+

### 部署步骤

#### 1. 启动基础设施

**启动MySQL**:

```bash
docker run -d --name mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  mysql:8.0
```

**启动Redis**:

```bash
docker run -d --name redis \
  -p 6379:6379 \
  redis:7-alpine
```

**启动Nacos**:

```bash
docker run -d --name nacos \
  -p 8848:8848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.2.3
```

#### 2. 初始化数据库

```bash
# 连接MySQL
mysql -u root -p

# 执行SQL脚本
source sql/schema.sql
source sql/init_data.sql
```

或直接执行：

```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/init_data.sql
```

#### 3. 配置Nacos（可选）

如果需要持久化Nacos配置，需要：

1. 在Nacos数据库中创建`nacos_config`数据库
2. 导入Nacos的初始化SQL脚本
3. 修改docker-compose.yml中Nacos的数据库配置

#### 4. 修改配置文件

各个服务的配置文件在 `src/main/resources/application.yml`

主要配置项：

**movie-resource-service**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/movie_ticket
    username: root
    password: root
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
```

**order-biz-service**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/movie_ticket
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
```

**gateway-service**:
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
```

#### 5. 启动服务

**启动顺序**:

1. **movie-resource-service**:

```bash
cd movie-resource-service
mvn spring-boot:run
```

2. **order-biz-service**:

```bash
cd order-biz-service
mvn spring-boot:run
```

3. **gateway-service**:

```bash
cd gateway-service
mvn spring-boot:run
```

#### 6. 验证部署

- 访问Nacos控制台，检查服务注册情况
- 通过网关访问接口，验证功能正常

## 环境变量配置

### Docker Compose环境变量

可以通过环境变量或`.env`文件配置：

```env
# Nacos配置
NACOS_SERVER_ADDR=nacos:8848
NACOS_NAMESPACE=
NACOS_GROUP=DEFAULT_GROUP

# MySQL配置
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DATABASE=movie_ticket
MYSQL_USERNAME=root
MYSQL_PASSWORD=root

# Redis配置
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=
```

### 应用环境变量

各服务支持通过环境变量覆盖配置：

```bash
# 启动时指定环境变量
export NACOS_SERVER_ADDR=localhost:8848
export MYSQL_HOST=localhost
export MYSQL_PASSWORD=root
java -jar gateway-service.jar
```

## 生产环境部署建议

### 1. 资源配置

**最小配置**:
- CPU: 2核
- 内存: 4GB
- 磁盘: 50GB

**推荐配置**:
- CPU: 4核
- 内存: 8GB
- 磁盘: 100GB SSD

### 2. 高可用部署

- **服务多实例**: 每个服务至少部署2个实例
- **数据库主从**: MySQL配置主从复制
- **Redis集群**: Redis部署集群模式
- **Nacos集群**: Nacos部署集群模式

### 3. 安全配置

- 修改默认密码
- 配置防火墙规则
- 使用HTTPS（通过Nginx反向代理）
- 配置SSL证书

### 4. 监控告警

- 集成Prometheus + Grafana监控
- 配置告警规则
- 日志聚合（ELK Stack）

### 5. 备份策略

- 数据库定时备份
- 配置文件版本管理
- 容器镜像备份

## 常见问题

### Q1: 服务启动失败，连接Nacos超时

**解决方案**:
- 检查Nacos是否正常启动
- 检查网络连接
- 检查防火墙规则
- 确认Nacos地址配置正确

### Q2: 数据库连接失败

**解决方案**:
- 检查MySQL是否正常启动
- 检查数据库用户权限
- 检查数据库是否存在
- 确认连接参数正确

### Q3: Redis连接失败

**解决方案**:
- 检查Redis是否正常启动
- 检查Redis密码配置
- 确认连接地址和端口正确

### Q4: 服务无法注册到Nacos

**解决方案**:
- 检查Nacos地址配置
- 检查服务端口是否被占用
- 查看服务启动日志
- 确认Nacos命名空间配置

### Q5: 网关路由失败

**解决方案**:
- 检查目标服务是否已注册
- 检查路由配置是否正确
- 查看网关日志
- 确认服务名称匹配

## 性能调优

### JVM参数

```bash
java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar service.jar
```

### 数据库连接池

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### Redis连接池

```yaml
spring:
  redis:
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
```

## 日志管理

### 日志配置

各服务日志默认输出到控制台，生产环境建议配置日志文件：

```yaml
logging:
  file:
    name: logs/movie-service.log
  level:
    root: INFO
    com.movie: DEBUG
```

### 日志收集

建议使用ELK Stack或Loki进行日志聚合和分析。

## 升级部署

### 滚动更新

```bash
# 1. 构建新版本
mvn clean package -DskipTests

# 2. 更新镜像
docker-compose build service-name

# 3. 重启服务（滚动更新）
docker-compose up -d --no-deps service-name
```

### 回滚

```bash
# 回滚到之前的版本
docker-compose down
docker-compose up -d
```

