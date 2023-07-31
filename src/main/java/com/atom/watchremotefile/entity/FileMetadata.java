package com.atom.watchremotefile.entity;

import java.time.LocalDateTime;
import java.util.Date;

public class FileMetadata {
    private Long id;
    private String linuxHostname;
    private String linuxFilePath;
    private Long linuxFileSize;
    private LocalDateTime linuxFileCreationTime;
    private LocalDateTime linuxFileLastModifiedTime;
    private String linuxFileOwner;
    private String linuxFilePermissions;
    private String ftpHostname;
    private String ftpUser;

    private String ftpFilePath;
    private Long ftpFileSize;
    private LocalDateTime ftpFileCreationTime;
    private LocalDateTime ftpFileLastModifiedTime;
    private String ftpFileOwner;
    private String ftpFilePermissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinuxHostname() {
        return linuxHostname;
    }

    public void setLinuxHostname(String linuxHostname) {
        this.linuxHostname = linuxHostname;
    }

    public String getLinuxFilePath() {
        return linuxFilePath;
    }

    public void setLinuxFilePath(String linuxFilePath) {
        this.linuxFilePath = linuxFilePath;
    }

    public Long getLinuxFileSize() {
        return linuxFileSize;
    }

    public void setLinuxFileSize(Long linuxFileSize) {
        this.linuxFileSize = linuxFileSize;
    }

    public LocalDateTime getLinuxFileCreationTime() {
        return linuxFileCreationTime;
    }

    public void setLinuxFileCreationTime(LocalDateTime linuxFileCreationTime) {
        this.linuxFileCreationTime = linuxFileCreationTime;
    }

    public LocalDateTime getLinuxFileLastModifiedTime() {
        return linuxFileLastModifiedTime;
    }

    public void setLinuxFileLastModifiedTime(LocalDateTime linuxFileLastModifiedTime) {
        this.linuxFileLastModifiedTime = linuxFileLastModifiedTime;
    }

    public String getLinuxFileOwner() {
        return linuxFileOwner;
    }

    public void setLinuxFileOwner(String linuxFileOwner) {
        this.linuxFileOwner = linuxFileOwner;
    }

    public String getLinuxFilePermissions() {
        return linuxFilePermissions;
    }

    public void setLinuxFilePermissions(String linuxFilePermissions) {
        this.linuxFilePermissions = linuxFilePermissions;
    }

    public String getFtpHostname() {
        return ftpHostname;
    }

    public void setFtpHostname(String ftpHostname) {
        this.ftpHostname = ftpHostname;
    }

    public String getFtpFilePath() {
        return ftpFilePath;
    }

    public void setFtpFilePath(String ftpFilePath) {
        this.ftpFilePath = ftpFilePath;
    }

    public Long getFtpFileSize() {
        return ftpFileSize;
    }

    public void setFtpFileSize(Long ftpFileSize) {
        this.ftpFileSize = ftpFileSize;
    }

    public LocalDateTime getFtpFileCreationTime() {
        return ftpFileCreationTime;
    }

    public void setFtpFileCreationTime(LocalDateTime ftpFileCreationTime) {
        this.ftpFileCreationTime = ftpFileCreationTime;
    }

    public LocalDateTime getFtpFileLastModifiedTime() {
        return ftpFileLastModifiedTime;
    }

    public void setFtpFileLastModifiedTime(LocalDateTime ftpFileLastModifiedTime) {
        this.ftpFileLastModifiedTime = ftpFileLastModifiedTime;
    }

    public String getFtpFileOwner() {
        return ftpFileOwner;
    }

    public void setFtpFileOwner(String ftpFileOwner) {
        this.ftpFileOwner = ftpFileOwner;
    }


    public String getFtpUser() {
        return ftpUser;
    }

    public void setFtpUser(String ftpUser) {
        this.ftpUser = ftpUser;
    }

    public String getFtpFilePermissions() {
        return ftpFilePermissions;
    }

    public void setFtpFilePermissions(String ftpFilePermissions) {
        this.ftpFilePermissions = ftpFilePermissions;
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
                "id=" + id +
                ", linuxHostname='" + linuxHostname + '\'' +
                ", linuxFilePath='" + linuxFilePath + '\'' +
                ", linuxFileSize=" + linuxFileSize +
                ", linuxFileCreationTime=" + linuxFileCreationTime +
                ", linuxFileLastModifiedTime=" + linuxFileLastModifiedTime +
                ", linuxFileOwner='" + linuxFileOwner + '\'' +
                ", linuxFilePermissions='" + linuxFilePermissions + '\'' +
                ", ftpHostname='" + ftpHostname + '\'' +
                ", ftpFilePath='" + ftpFilePath + '\'' +
                ", ftpFileSize=" + ftpFileSize +
                ", ftpFileCreationTime=" + ftpFileCreationTime +
                ", ftpFileLastModifiedTime=" + ftpFileLastModifiedTime +
                ", ftpFileOwner='" + ftpFileOwner + '\'' +
                ", ftpUser='" + ftpUser + '\'' +
                ", ftpFilePermissions='" + ftpFilePermissions + '\'' +
                '}';
    }
}