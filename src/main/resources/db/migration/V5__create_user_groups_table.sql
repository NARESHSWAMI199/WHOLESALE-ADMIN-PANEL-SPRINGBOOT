-- test.user_groups definition

CREATE TABLE `user_groups` (
  `user_id` int DEFAULT NULL,
  `group_id` int DEFAULT NULL,
  PRIMARY KEY(user_id,group_id),
  FOREIGN kEY(user_id) REFERENCES users(user_id),
  FOREIGN kEY(group_id) REFERENCES groups(id),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=167 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;