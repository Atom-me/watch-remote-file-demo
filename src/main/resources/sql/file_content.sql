CREATE TABLE file_content
(
    id         bigint AUTO_INCREMENT PRIMARY KEY,
    file_path  VARCHAR(255) NOT NULL,
    content    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);