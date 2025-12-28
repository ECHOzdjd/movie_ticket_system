# 容错机制说明

## 概述

系统使用 Resilience4j 实现容错机制，主要包括：

1. **熔断器（Circuit Breaker）**: 防止服务雪崩
2. **降级处理（Fallback）**: 服务不可用时的降级策略
3. **负载均衡**: 通过 Spring Cloud LoadBalancer 实现

## 实现位置

### 1. Feign 客户端容错

**位置**: `order-biz-service` 中的 `MovieResourceFeignClient`

**实现方式**:
- 使用 `@FeignClient` 的 `fallback` 属性指定降级类
- 降级类: `MovieResourceFeignClientFallback`

**配置**:
```yaml
feign:
  circuitbreaker:
    enabled: true  # 启用熔断器

resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10  # 滑动窗口大小
        minimumNumberOfCalls: 5  # 最小调用次数
        waitDurationInOpenState: 10s  # 熔断开启状态持续时间
        failureRateThreshold: 50  # 失败率阈值（50%）
  timelimiter:
    configs:
      default:
        timeoutDuration: 3s  # 超时时间
```

### 2. 负载均衡

**实现方式**:
- Gateway 路由使用 `lb://service-name` 协议
- 自动通过 Nacos 服务发现进行负载均衡
- 使用 Spring Cloud LoadBalancer 作为客户端负载均衡器

**配置示例**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-biz-service
          uri: lb://order-biz-service  # lb:// 表示负载均衡
```

## 容错流程

1. **正常状态**: 请求正常转发到目标服务
2. **异常累积**: 当失败率达到阈值（50%）时，触发熔断
3. **熔断开启**: 熔断器开启，直接返回降级结果，不调用实际服务
4. **半开状态**: 经过配置的时间（10秒）后，进入半开状态，允许少量请求通过
5. **恢复正常**: 如果半开状态下的请求成功，熔断器关闭，恢复正常

## 降级策略

当 `movie-resource-service` 不可用时：

- **查询场次详情**: 返回"服务暂时不可用，请稍后重试"
- **增加/减少已售座位数**: 返回"服务暂时不可用，请稍后重试"

## 监控建议

生产环境建议：
1. 集成 Micrometer 监控熔断器状态
2. 使用 Prometheus + Grafana 可视化监控
3. 配置告警规则

