-- 创建数据库
CREATE DATABASE IF NOT EXISTS movie_ticket DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE movie_ticket;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 影片表
CREATE TABLE IF NOT EXISTS `movie` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(200) NOT NULL COMMENT '影片名称',
    `type` VARCHAR(50) COMMENT '影片类型',
    `director` VARCHAR(100) COMMENT '导演',
    `actors` VARCHAR(500) COMMENT '主演',
    `description` TEXT COMMENT '简介',
    `duration` INT COMMENT '时长（分钟）',
    `rating` DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分',
    `cover_image` VARCHAR(500) COMMENT '封面图片',
    `release_date` DATETIME COMMENT '上映日期',
    `is_released` TINYINT(1) DEFAULT 0 COMMENT '是否上映',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_release_date` (`release_date`),
    INDEX `idx_is_released` (`is_released`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影片表';

-- 影院表
CREATE TABLE IF NOT EXISTS `cinema` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(200) NOT NULL COMMENT '影院名称',
    `address` VARCHAR(500) COMMENT '地址',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `business_hours` VARCHAR(100) COMMENT '营业时间',
    `description` TEXT COMMENT '影院介绍',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影院表';

-- 场次表
CREATE TABLE IF NOT EXISTS `schedule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `movie_id` BIGINT NOT NULL COMMENT '影片ID',
    `cinema_id` BIGINT NOT NULL COMMENT '影院ID',
    `hall_name` VARCHAR(100) COMMENT '放映厅名称',
    `show_time` DATETIME NOT NULL COMMENT '放映时间',
    `price` DECIMAL(10,2) NOT NULL COMMENT '票价',
    `total_seats` INT NOT NULL DEFAULT 0 COMMENT '总座位数',
    `sold_seats` INT NOT NULL DEFAULT 0 COMMENT '已售座位数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_movie_id` (`movie_id`),
    INDEX `idx_cinema_id` (`cinema_id`),
    INDEX `idx_show_time` (`show_time`),
    FOREIGN KEY (`movie_id`) REFERENCES `movie`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`cinema_id`) REFERENCES `cinema`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场次表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `schedule_id` BIGINT NOT NULL COMMENT '场次ID',
    `seats` TEXT NOT NULL COMMENT '座位信息（JSON格式）',
    `seat_count` INT NOT NULL COMMENT '座位数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已完成，2-已取消',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `pay_time` DATETIME COMMENT '支付时间',
    `expire_time` DATETIME COMMENT '过期时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_schedule_id` (`schedule_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_expire_time` (`expire_time`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`schedule_id`) REFERENCES `schedule`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

