# API 文档

本文档描述电影票购票系统的所有API接口。

## 基础信息

- **Base URL**: `http://localhost:9999/api` (通过网关)
- **认证方式**: Bearer Token (JWT)
- **Content-Type**: `application/json`

## 认证说明

部分接口需要携带 JWT Token 进行认证：

**公开接口**（无需 Token）：
- 用户注册 `/api/user/register`
- 用户登录 `/api/user/login`
- 电影查询 `/api/movie/**`
- 影院查询 `/api/cinema/**`
- 排期查询 `/api/schedule/**`

**受保护接口**（需要 Token）：
- 用户信息 `/api/user/info`
- 座位操作 `/api/seat/**`
- 订单操作 `/api/order/**`

**Token 使用方式**：
```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  http://localhost:9999/api/user/info
```

## 统一响应格式

所有API响应遵循以下格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 响应码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 500 | 服务器错误 |

## 用户相关接口

### 1. 用户注册

**接口地址**: `POST /api/user/register`

**请求参数**:

```json
{
  "username": "testuser",
  "password": "123456",
  "phone": "13800138000",
  "email": "test@example.com"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "phone": "13800138000",
    "email": "test@example.com",
    "nickname": "testuser",
    "createTime": "2024-01-01T10:00:00"
  }
}
```

### 2. 用户登录

**接口地址**: `POST /api/user/login`

**请求参数**:

```json
{
  "username": "testuser",
  "password": "123456"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 3. 获取当前用户信息

**接口地址**: `GET /api/user/info`

**请求头**: 
- `Authorization: Bearer {token}`

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "phone": "13800138000",
    "email": "test@example.com",
    "nickname": "testuser"
  }
}
```

## 影片相关接口

### 1. 查询上映影片列表

**接口地址**: `GET /api/movie/list`

**请求参数**:
- `current` (可选): 当前页，默认1
- `size` (可选): 每页数量，默认10

**示例**: `GET /api/movie/list?current=1&size=10`

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "流浪地球2",
        "type": "科幻",
        "director": "郭帆",
        "actors": "吴京,刘德华,李雪健",
        "duration": 173,
        "rating": 8.3,
        "coverImage": "https://example.com/cover1.jpg",
        "releaseDate": "2023-01-22T00:00:00",
        "isReleased": true
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 查询影片详情

**接口地址**: `GET /api/movie/{id}`

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "流浪地球2",
    "type": "科幻",
    "director": "郭帆",
    "actors": "吴京,刘德华,李雪健",
    "description": "太阳即将毁灭...",
    "duration": 173,
    "rating": 8.3,
    "coverImage": "https://example.com/cover1.jpg",
    "releaseDate": "2023-01-22T00:00:00",
    "isReleased": true
  }
}
```

## 影院相关接口

### 1. 查询所有影院

**接口地址**: `GET /api/cinema/list`

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "万达影城（CBD店）",
      "address": "北京市朝阳区建国路93号万达广场",
      "phone": "010-12345678",
      "businessHours": "09:00-24:00",
      "description": "位于CBD核心区域..."
    }
  ]
}
```

### 2. 查询影院详情

**接口地址**: `GET /api/cinema/{id}`

## 场次相关接口

### 1. 根据影片ID查询场次

**接口地址**: `GET /api/schedule/movie/{movieId}`

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "movieId": 1,
      "cinemaId": 1,
      "hallName": "1号厅",
      "showTime": "2024-01-01T14:00:00",
      "price": 45.00,
      "totalSeats": 200,
      "soldSeats": 50
    }
  ]
}
```

### 2. 根据影院ID查询场次

**接口地址**: `GET /api/schedule/cinema/{cinemaId}`

### 3. 查询场次详情

**接口地址**: `GET /api/schedule/{id}`

## 座位相关接口

### 1. 查询场次座位图

**接口地址**: `GET /api/seat/map/{scheduleId}`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "scheduleId": 1,
    "totalSeats": 200,
    "soldSeats": 50,
    "availableSeats": 145,
    "lockedSeats": ["A1", "A2"]
  }
}
```

### 2. 锁定座位

**接口地址**: `POST /api/seat/lock`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:

```json
{
  "scheduleId": 1,
  "seatNumbers": ["A1", "A2", "A3"]
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

## 订单相关接口

### 1. 创建订单

**接口地址**: `POST /api/order/create`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:

```json
{
  "scheduleId": 1,
  "seatNumbers": ["A1", "A2", "A3"]
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "ORD2024010112000012345678",
    "userId": 1,
    "scheduleId": 1,
    "seats": "[\"A1\",\"A2\",\"A3\"]",
    "seatCount": 3,
    "totalAmount": 135.00,
    "status": 0,
    "createTime": "2024-01-01T12:00:00",
    "expireTime": "2024-01-01T12:15:00"
  }
}
```

### 2. 支付订单

**接口地址**: `POST /api/order/pay/{orderNo}`

**请求头**: `Authorization: Bearer {token}`

### 3. 取消订单

**接口地址**: `POST /api/order/cancel/{orderNo}`

**请求头**: `Authorization: Bearer {token}`

### 4. 查询订单详情

**接口地址**: `GET /api/order/{orderNo}`

**请求头**: `Authorization: Bearer {token}`

### 5. 查询订单列表

**接口地址**: `GET /api/order/list`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "orderNo": "ORD2024010112000012345678",
      "userId": 1,
      "scheduleId": 1,
      "seats": "[\"A1\",\"A2\",\"A3\"]",
      "seatCount": 3,
      "totalAmount": 135.00,
      "status": 1,
      "createTime": "2024-01-01T12:00:00",
      "payTime": "2024-01-01T12:05:00"
    }
  ]
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未授权，请先登录 |
| 402 | Token无效或已过期 |
| 403 | 无权限访问 |
| 1000 | 用户不存在 |
| 1001 | 用户已存在 |
| 1002 | 密码错误 |
| 1100 | 影片不存在 |
| 1101 | 场次不存在 |
| 1102 | 影院不存在 |
| 1200 | 座位已被锁定 |
| 1201 | 座位不可用 |
| 1300 | 订单不存在 |
| 1301 | 订单状态错误 |
| 1302 | 订单已过期 |
| 1303 | 订单已取消 |

## 注意事项

1. 所有需要认证的接口都必须在请求头中携带 `Authorization: Bearer {token}`
2. Token有效期为24小时
3. 座位锁定时间为5分钟，超时自动释放
4. 订单支付时间为15分钟，超时自动取消
5. 所有时间字段均为ISO 8601格式

