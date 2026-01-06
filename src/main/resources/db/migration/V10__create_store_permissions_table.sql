-- test.store_permissions definition

CREATE TABLE `store_permissions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `permission` varchar(50) DEFAULT NULL,
  `default_permission` enum('Y','N') DEFAULT NULL,
  `permission_for` varchar(100) NOT NULL DEFAULT 'Others',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;