CREATE TABLE `file_metadata`
(
    `id`                            bigint       NOT NULL AUTO_INCREMENT COMMENT '文件元数据 ID，自动递增',
    `linux_hostname`                varchar(255) NOT NULL COMMENT 'Linux 主机名',
    `linux_file_path`               varchar(255) NOT NULL COMMENT 'Linux 文件路径',
    `linux_file_size`               bigint       NOT NULL COMMENT 'Linux 文件大小',
    `linux_file_creation_time`      datetime     NOT NULL COMMENT 'Linux 文件创建时间',
    `linux_file_last_modified_time` datetime     NOT NULL COMMENT 'Linux 文件最后修改时间',
    `linux_file_owner`              varchar(255) DEFAULT NULL COMMENT 'Linux 文件所有者',
    `linux_file_permissions`        varchar(255) DEFAULT NULL COMMENT 'Linux 文件权限',
    `ftp_hostname`                  varchar(255) NOT NULL COMMENT 'FTP 主机名',
    `ftp_user`                  varchar(255) NOT NULL COMMENT 'FTP 用户名',
    `ftp_file_path`                 varchar(255) NOT NULL COMMENT 'FTP 文件路径',
    `ftp_file_size`                 bigint       NOT NULL COMMENT 'FTP 文件大小',
    `ftp_file_creation_time`        datetime     NOT NULL COMMENT 'FTP 文件创建时间',
    `ftp_file_last_modified_time`   datetime     DEFAULT NULL COMMENT 'FTP 文件最后修改时间',
    `ftp_file_owner`                varchar(255) DEFAULT NULL COMMENT 'FTP 文件所有者',
    `ftp_file_permissions`          varchar(255) DEFAULT NULL COMMENT 'FTP 文件权限',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='文件元数据表，用于存储 Linux 文件和 FTP 文件的元数据信息。';