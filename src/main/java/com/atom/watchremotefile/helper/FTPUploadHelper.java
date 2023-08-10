package com.atom.watchremotefile.helper;

import com.atom.watchremotefile.config.FtpProperties;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Atom
 */
@Component
public class FTPUploadHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FTPUploadHelper.class);

    @Autowired
    private FtpProperties ftpProperties;

    private FTPClient ftpClient;

    public FTPClient getFtpClient() throws IOException {
        if (ftpClient == null || !ftpClient.isConnected()) {
            ftpClient = new FTPClient();
            connectAndLogin();
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setConnectTimeout(10000);
            ftpClient.setDataTimeout(10000);
        }
        return ftpClient;
    }

    private void connectAndLogin() throws IOException {
        ftpClient.connect(ftpProperties.getHost(), ftpProperties.getPort());
        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new IOException("FTP server refused connection: " + replyCode);
        }
        boolean success = ftpClient.login(ftpProperties.getUsername(), ftpProperties.getPassword());
        if (!success) {
            throw new IOException("FTP server login failed: " + ftpClient.getReplyString());
        }
    }

    public void uploadFile(String fileName, String fileContent) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes())) {
            FTPClient ftpClient = getFtpClient();

            // 创建父目录
            String remoteFilePath = ftpProperties.getRemoteDirectory() + fileName;
            createParentDirectories(ftpClient, remoteFilePath);

            // 文件上穿
            boolean stored = ftpClient.storeFile(ftpProperties.getRemoteDirectory() + fileName, inputStream);
            LOGGER.info("Upload content to FTP result: [{}]", stored);
        } catch (IOException e) {
            LOGGER.error("Failed to upload file to FTP: {}", e.getMessage());
        }
    }

    private void createParentDirectories(FTPClient ftpClient, String remoteFilePath) throws IOException {
        String parentDirectory = remoteFilePath.substring(0, remoteFilePath.lastIndexOf('/'));
        if (!ftpClient.changeWorkingDirectory(parentDirectory)) {
            createParentDirectories(ftpClient, parentDirectory);
            boolean made = ftpClient.makeDirectory(parentDirectory);
            LOGGER.info("Made directory result: [{}]", made);
        }
    }

    @PreDestroy
    public void disconnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                LOGGER.error("Failed to disconnect from FTP: {}", e.getMessage());
            }
        }
    }
}