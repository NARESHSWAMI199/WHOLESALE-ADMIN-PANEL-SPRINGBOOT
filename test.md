


# Address


-- test.address definition

CREATE TABLE `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(50) DEFAULT NULL,
  `city` int DEFAULT NULL,
  `state` int DEFAULT NULL,
  `latitude` float DEFAULT NULL,
  `altitude` float DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


# City 

-- test.city definition

CREATE TABLE `city` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city_name` varchar(50) DEFAULT NULL,
  `state_id` int DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


# Item


-- test.item definition

CREATE TABLE `item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `wholesale_id` int DEFAULT NULL,
  `label` enum('O','N') DEFAULT NULL,
  `price` float NOT NULL,
  `discount` float DEFAULT NULL,
  `description` text NOT NULL,
  `avatar` text,
  `rating` float DEFAULT NULL,
  `status` enum('A','D') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `created_at` bigint NOT NULL,
  `created_by` int NOT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `slug` varchar(50) DEFAULT NULL,
  `in_stock` enum('Y','N') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `wholesale_id` (`wholesale_id`),
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`wholesale_id`) REFERENCES `store` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Slips

-- test.slips definition

CREATE TABLE `slips` (
  `id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(50) NOT NULL,
  `item_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `wholesale_id` int DEFAULT NULL,
  `status` enum('S','P') DEFAULT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# State

-- test.state definition

CREATE TABLE `state` (
  `id` int NOT NULL AUTO_INCREMENT,
  `state_name` varchar(50) DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


# Store

-- test.store definition

CREATE TABLE `store` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `slug` varchar(50) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `avtar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `address` int DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(12) DEFAULT NULL,
  `discription` text,
  `rating` float DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `store_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# User


-- test.`user` definition

CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(50) DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(50) NOT NULL,
  `contact` varchar(12) NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `avtar` text,
  `user_type` enum('R','S','W') DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

