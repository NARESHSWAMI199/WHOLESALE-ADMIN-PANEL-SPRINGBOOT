
CREATE TABLE `group_permissions` (
  `group_id` int DEFAULT NULL,
  `permission_id` int DEFAULT NULL,
  PRIMARY KEY (`group_id`,`permission_id`),
  FOREIGN KEY (group_id) REFERENCES groups(id),
  FOREIGN KEY (permission_id) REFERENCES group_permissions(id)
);