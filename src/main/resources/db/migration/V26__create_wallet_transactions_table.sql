
CREATE TABLE `wholesaler_wallet` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `amount` float DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY user_id REFERENCES users(user_id),
  UNIQUE KEY `user_id` (`user_id`)
);