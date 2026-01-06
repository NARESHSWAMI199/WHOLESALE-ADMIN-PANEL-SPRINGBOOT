-- test.wholesaler_permissions definition

CREATE TABLE `wholesaler_permissions` (
  `user_id` int DEFAULT NULL,
  `permission_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`,`permission_id`),
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (permission_id) REFERENCES store_permissions(id)
) ENGINE=InnoDB AUTO_INCREMENT=691 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;