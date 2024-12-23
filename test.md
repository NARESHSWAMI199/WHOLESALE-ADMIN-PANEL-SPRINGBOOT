


# Address


-- test.address definition
```sql
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

```
# City 

-- test.city definition
```sql
CREATE TABLE `city` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city_name` varchar(50) DEFAULT NULL,
  `state_id` int DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

# Item


-- test.item definition
```sql
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
```


# Slips

-- test.slips definition
```sql
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

```

# State

-- test.state definition
```sql
CREATE TABLE `state` (
  `id` int NOT NULL AUTO_INCREMENT,
  `state_name` varchar(50) DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

```
# Store

-- test.store definition
```sql
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

```

# User


-- test.`user` definition
```sql
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
```
# Groups permissions

```sql 

create table `groups` (
	id int primary key auto_increment,
	slug varchar(50) unique not null,
	name varchar(50),
	is_deleted enum('Y','N'),
	created_at bigint,
	created_by int,
	updated_at bigint,
	updated_by int
);





create table permissions (
	id int primary key auto_increment,
	permission varchar(50),
	access_url text ,
  permission_for varchar(20)
);




create table group_permissions (
	group_id int,
	permission_id int
);


create table user_groups(
	user_id int,
	group_id int
);

```

# All permissions


```sql


       
insert into permissions (`permission`,`access_url`,`permission_for`) values

-- ('List','/admin/auth/all','User'),
-- ('Create','/admin/auth/create','User'),
-- ('Edit','/admin/auth/update','User'),
-- ('Detail','/admin/auth/detail','User'),
-- ('Status','/admin/auth/status','User'),
-- ('Delete','/admin/auth/delete','User'),
-- 
-- ('List','/admin/store/all','Store'),
-- ('Create','/admin/store/create','Store'),
-- ('Edit','/admin/store/update','Store'),
-- ('Detail','/admin/store/detail','Store'),
-- ('Status','/admin/store/status','Store'),
-- ('Delete','/admin/store/delete','Store'),
-- 
-- ('List','/admin/item/all','Item'),
-- ('Create','/admin/item/create','Item'),
-- ('Edit','/admin/item/update','Item'),
-- ('Detail','/admin/item/detail','Item'),
-- ('Status','/admin/item/status','Item'),
-- ('Delete','/admin/item/delete','Item'),
-- ('Stock','/admin/item/stock','Item'),
-- 
-- 
-- ('List','/group/all','Group'),
-- ('Create','/group/create','Group'),
-- ('Edit','/group/update','Group'),
-- ('Detail','/group/detail','Group'),
-- ('Status','/group/status','Group'),
-- ('Delete','/group/delete','Group'),
-- 
-- 
-- ('List','/admin/permissions/all','Permissions'),
-- 
--  ('List','/admin/dashboard','Dashboard'),    
    ('City List','/admin/address/city','Address'),
    ('State List','/admin/address/state','Address');
       


```
# Promotions

```sql 

   create table store_promotions(
   		id int primary key auto_increment,
   		banner_img text,
   		promotion_type enum ('I','S'),
   		store_id int,
   		item_id int,
   		priority enum('1','2','3','4','5'),
   		priority_hours bigint,
   		max_repeat int default 10,
   		state int,
   		city int,
   		created_date bigint,
   		expiry_date bigint,
   		created_by int
   );
       








create table item_subcategory (
	id int primary key auto_increment,
	category_id int,
	subcategroy varchar(250),
	icon text
);



create table store_category (
	id int primary key auto_increment,
	categroy varchar(250),
	icon text
);

alter table item_category add column icon text;


create table service_plans (
	id int primary key auto_increment,
	name varchar(500),
	price float,
	discount float,
	status enum ('A','D'),
	icon text,
	months int,
	description text,
	is_deleted enum('Y','N'),
	created_at bigint,
	created_by int,
	updated_at bigint,
	updated_by int
);


create table store_subcategory (
	id int primary key auto_increment,
	category_id int,
	subcategroy varchar(250),
	icon text
);


create table plan_offers  (
	id int primary key auto_increment,
	plan_id int,
	name varchar(500),
	discount float,
	status enum ('A','D'),
	description text,	
	start_date bigint,
	expiry_date bigint,
	created_at bigint,
	created_by int,
	updated_at bigint,
	updated_by int
);





















```