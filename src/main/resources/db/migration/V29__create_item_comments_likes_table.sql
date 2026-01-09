CREATE TABLE `item_comment_likes` (
    `comment_id` bigint DEFAULT NULL,
    `user_id` int DEFAULT NULL,
    PRIMARY KEY (`comment_id`,`user_id`),
    FOREIGN KEY (comment_id) REFERENCES item_comments(id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

