
CREATE TABLE `user_groups` (
  `user_id` int DEFAULT NULL,
  `group_id` int DEFAULT NULL,
  PRIMARY KEY(user_id,group_id),
  FOREIGN kEY(user_id) REFERENCES users(user_id),
  FOREIGN kEY(group_id) REFERENCES groups(id),
);