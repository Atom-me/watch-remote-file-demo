package com.atom.watchremotefile.mapper;

import com.atom.watchremotefile.entity.FileMetadata;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

/**
 * @author Atom
 */
public interface FileMetadataMapper {
    @Insert("INSERT INTO file_metadata (linux_hostname, " +
            "linux_file_path, " +
            "linux_file_size," +
            " linux_file_creation_time," +
            " linux_file_last_modified_time, " +
            "linux_file_owner, " +
            "linux_file_permissions, " +
            "ftp_hostname, " +
            "ftp_user , " +
            "ftp_file_path, " +
            "ftp_file_size, " +
            "ftp_file_creation_time, " +
            "ftp_file_last_modified_time, " +
            "ftp_file_owner, " +
            "ftp_file_permissions) " +
            "VALUES (" +
            "#{linuxHostname}," +
            "#{linuxFilePath}," +
            "#{linuxFileSize}," +
            "#{linuxFileCreationTime}," +
            "#{linuxFileLastModifiedTime}," +
            "#{linuxFileOwner}," +
            "#{linuxFilePermissions}," +
            "#{ftpHostname}," +
            "#{ftpUser}," +
            "#{ftpFilePath}," +
            "#{ftpFileSize}," +
            "#{ftpFileCreationTime}," +
            "#{ftpFileLastModifiedTime}," +
            "#{ftpFileOwner}," +
            "#{ftpFilePermissions})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FileMetadata fileMetadata);
}
