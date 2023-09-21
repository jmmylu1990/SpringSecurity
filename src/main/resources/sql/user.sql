DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
      `username` varchar(50) NOT NULL COMMENT '用戶名稱',
      `password` varchar(64) COMMENT '密碼',
      `mobile` varchar(20) COMMENT '手機號',
      `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '用戶是否可用',
      `roles` text COMMENT '用戶角色，多個角色之間以逗號隔開',
      PRIMARY KEY (`id`),
      KEY `index_username`(`username`),
      KEY `index_mobile`(`mobile`)
) COMMENT '用戶表';

-- 密码明文都为 123456
INSERT INTO `security`.`user` VALUES ('1', 'admin', '$2a$10$JNVWTh5Yq56kJtrCZkcDk.DL/L/i8g3KrTAshcHW3mFf8//lnfG56', '11111111111', '1', 'ROLE_ADMIN,ROLE_USER',null,null);
INSERT INTO `security`.`user` VALUES ('2', 'user', '$2a$10$JNVWTh5Yq56kJtrCZkcDk.DL/L/i8g3KrTAshcHW3mFf8//lnfG56', '22222222222', '1', 'ROLE_USER',null,null);