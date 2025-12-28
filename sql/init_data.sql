-- 初始化测试数据

USE movie_ticket;

-- 插入测试用户
INSERT INTO `user` (`username`, `password`, `phone`, `email`, `nickname`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwKB4bYm', '13800138000', 'admin@example.com', '管理员'),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwKB4bYm', '13900139000', 'test@example.com', '测试用户');
-- 密码为: 123456

-- 插入测试影片
INSERT INTO `movie` (`name`, `type`, `director`, `actors`, `description`, `duration`, `rating`, `cover_image`, `release_date`, `is_released`) VALUES
('流浪地球2', '科幻', '郭帆', '吴京,刘德华,李雪健', '太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新家园。然而宇宙之路危险重重，流浪地球时代的年轻人再次挺身而出，展开争分夺秒的生死之战。', 173, 8.3, 'https://example.com/cover1.jpg', '2023-01-22 00:00:00', 1),
('满江红', '历史', '张艺谋', '沈腾,易烊千玺,张译', '南宋绍兴年间，岳飞死后四年，秦桧率兵与金国会谈。会谈前夜，金国使者死在宰相驻地，所携密信也不翼而飞。小兵张大与亲兵营副统领孙均机缘巧合被裹挟进这巨大阴谋之中。', 159, 8.2, 'https://example.com/cover2.jpg', '2023-01-22 00:00:00', 1),
('深海', '动画', '田晓鹏', '苏鑫,王亭文', '为心结所困的少女（参宿）误入梦幻深海世界，与来自"深海"的魔法船长南河，在一场奇幻之旅中寻找生命的答案。', 112, 7.3, 'https://example.com/cover3.jpg', '2023-01-22 00:00:00', 1);

-- 插入测试影院
INSERT INTO `cinema` (`name`, `address`, `phone`, `business_hours`, `description`) VALUES
('万达影城（CBD店）', '北京市朝阳区建国路93号万达广场', '010-12345678', '09:00-24:00', '位于CBD核心区域，交通便利，设备先进'),
('CGV影城（三里屯店）', '北京市朝阳区三里屯太古里南区', '010-87654321', '10:00-23:00', '国际化影城，观影体验极佳'),
('UME影城（双井店）', '北京市朝阳区双井桥东富力广场', '010-11223344', '09:30-23:30', '老牌影院，价格实惠');

-- 插入测试场次
INSERT INTO `schedule` (`movie_id`, `cinema_id`, `hall_name`, `show_time`, `price`, `total_seats`, `sold_seats`) VALUES
(1, 1, '1号厅', DATE_ADD(NOW(), INTERVAL 2 HOUR), 45.00, 200, 0),
(1, 1, '2号厅', DATE_ADD(NOW(), INTERVAL 5 HOUR), 50.00, 150, 0),
(2, 1, '3号厅', DATE_ADD(NOW(), INTERVAL 3 HOUR), 48.00, 180, 0),
(2, 2, 'IMAX厅', DATE_ADD(NOW(), INTERVAL 4 HOUR), 80.00, 300, 0),
(3, 2, '1号厅', DATE_ADD(NOW(), INTERVAL 6 HOUR), 40.00, 120, 0),
(1, 3, 'VIP厅', DATE_ADD(NOW(), INTERVAL 1 DAY), 60.00, 100, 0);

