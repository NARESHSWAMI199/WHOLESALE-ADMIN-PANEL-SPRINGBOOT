CREATE TABLE `contact_list` (
    `user_id` int DEFAULT NULL,
    `contact_id` int DEFAULT NULL,
    PRIMARY KEY (`user_id`,`contact_id`),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (contact_id) REFERENCES users(user_id)
);

