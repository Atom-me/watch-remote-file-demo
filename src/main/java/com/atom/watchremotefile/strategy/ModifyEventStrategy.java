package com.atom.watchremotefile.strategy;

import com.atom.watchremotefile.config.FtpProperties;
import com.atom.watchremotefile.config.RemoteLinuxProperties;
import com.atom.watchremotefile.entity.FileContent;
import com.atom.watchremotefile.entity.FileMetadata;
import com.atom.watchremotefile.helper.FTPUploadHelper;
import com.atom.watchremotefile.mapper.FileContentMapper;
import com.atom.watchremotefile.mapper.FileMetadataMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Atom
 */
@Component
public class ModifyEventStrategy implements FileEventStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyEventStrategy.class);

    @Autowired
    private FTPUploadHelper ftpUploadHelper;

    @Autowired
    private FileMetadataMapper fileMetadataMapper;
    @Autowired
    private FileContentMapper fileContentMapper;

    @Autowired
    private RemoteLinuxProperties remoteLinuxProperties;
    @Autowired
    private FtpProperties ftpProperties;

    @Override
    public void handleEvent(Session session, String remoteFilePath) {
        try {
            Map<String, String> originFileAttributes = getFileAttributes(session, remoteFilePath);

            // 从数据库中获取文件内容
            FileContent previousFileContent = fileContentMapper.findFileContentByFilePath(remoteFilePath);
            String previousContent = previousFileContent != null ? previousFileContent.getContent() : null;
            // 获取当前文件内容
            String currentContent = getFileContent(session, remoteFilePath);

            String newFtpFile = remoteFilePath + "-" + System.nanoTime();

            // 对比当前文件内容和上次文件内容
            if (previousContent != null && !previousContent.equals(currentContent)) {
                LOGGER.info("[{}] File Content Updated.", remoteFilePath);

                // 将文件内容按行处理
                List<String> previousLines = Arrays.asList(previousContent.split("\\r?\\n"));
                List<String> currentLines = Arrays.asList(currentContent.split("\\r?\\n"));

                // 对比每行数据的不同
                for (int i = 0; i < Math.min(previousLines.size(), currentLines.size()); i++) {
                    if (!previousLines.get(i).equals(currentLines.get(i))) {
                        LOGGER.info("Line [{}] changed:", (i + 1));
                        LOGGER.info("Before: [{}]", previousLines.get(i));
                        LOGGER.info("After: [{}]", currentLines.get(i));
                    }
                }

                // 获取删除的行
                for (int i = Math.min(previousLines.size(), currentLines.size()); i < previousLines.size(); i++) {
                    LOGGER.info("Line [{}] removed:", (i + 1));
                    LOGGER.info("Before: [{}]", previousLines.get(i));
                }

                // 获取新增的行
                for (int i = Math.min(previousLines.size(), currentLines.size()); i < currentLines.size(); i++) {
                    LOGGER.info("Line [{}] added:", (i + 1));
                    LOGGER.info("After: [{}]", currentLines.get(i));
                    LOGGER.info("start upload updated content to ftp.");
                    // new fileName
                    // todo  send to kafka

                    ftpUploadHelper.uploadFile(newFtpFile, currentLines.get(i));
                }

            } else {
                LOGGER.info("File Content:\n" + currentContent);
            }

            Map<String, String> ftpFileAttributes = getFtpFileAttributes(ftpProperties.getRemoteDirectory() + newFtpFile);
            storeFileMetadata(remoteFilePath, originFileAttributes, newFtpFile, ftpFileAttributes);

            storeFileContent(remoteFilePath, currentContent);

        } catch (JSchException | IOException e) {
            //todo
            LOGGER.error("ERROR:::::", e);
        }
    }

    private void storeFileContent(String remoteFilePath, String currentContent) {
        // 更新文件内容时，将新内容存储到数据库中
        FileContent fileContent = new FileContent();
        fileContent.setFilePath(remoteFilePath);
        fileContent.setContent(currentContent);
        fileContentMapper.insertFileContent(fileContent);
    }

    /**
     * 保存文件元数据信息
     *
     * @param remoteFilePath
     * @param originFileAttributes
     * @param newFtpFile
     * @param ftpFileAttributes
     */
    private void storeFileMetadata(String remoteFilePath, Map<String, String> originFileAttributes, String newFtpFile, Map<String, String> ftpFileAttributes) {
        // Store the file attributes in MySQL
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setLinuxFilePath(remoteFilePath);
        fileMetadata.setLinuxFilePermissions(originFileAttributes.get("Permissions"));
        fileMetadata.setLinuxFileOwner(originFileAttributes.get("Owner"));
        fileMetadata.setLinuxFileSize(Long.parseLong(originFileAttributes.get("Size")));
        fileMetadata.setLinuxFileLastModifiedTime(LocalDateTime.parse(originFileAttributes.get("Last Modified Time").replace(" ", "T")));
        fileMetadata.setLinuxFileCreationTime(LocalDateTime.now());
        fileMetadata.setLinuxHostname(remoteLinuxProperties.getHostname());
        fileMetadata.setFtpUser(ftpProperties.getUsername());
        fileMetadata.setFtpFilePath(ftpProperties.getRemoteDirectory() + newFtpFile);
        fileMetadata.setFtpFilePermissions(ftpFileAttributes.get("Permissions"));
        fileMetadata.setFtpFileOwner(ftpFileAttributes.get("Owner"));
        fileMetadata.setFtpFileSize(Objects.isNull(ftpFileAttributes.get("Size")) ? 0 : Long.parseLong(ftpFileAttributes.get("Size")));
        fileMetadata.setFtpFileLastModifiedTime(Objects.isNull(ftpFileAttributes.get("Last Modified Time")) ? null : LocalDateTime.parse(ftpFileAttributes.get("Last Modified Time").replace(" ", "T")));
        fileMetadata.setFtpFileCreationTime(LocalDateTime.now());
        fileMetadata.setFtpHostname(ftpProperties.getHost());
        fileMetadataMapper.insert(fileMetadata);
    }


    private static Map<String, String> getFileAttributes(Session session, String remoteFilePath) throws JSchException, IOException {
        Map<String, String> fileAttributes = new HashMap<>();

        String command = "ls -l --time-style=long-iso " + remoteFilePath;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()))) {
            channel.connect();
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 8) {
                    fileAttributes.put("Permissions", parts[0]);
                    fileAttributes.put("Owner", parts[2]);
                    fileAttributes.put("Group", parts[3]);
                    fileAttributes.put("Size", parts[4]);
                    fileAttributes.put("Last Modified Time", parts[5] + " " + parts[6]);
                }
            }

            channel.disconnect();
        }

        return fileAttributes;
    }

    private Map<String, String> getFtpFileAttributes(String ftpFilePath) throws IOException {
        Map<String, String> fileAttributes = new HashMap<>();
        FTPFile[] files = ftpUploadHelper.getFtpClient().listFiles(ftpFilePath);
        if (files.length > 0) {
            FTPFile file = files[0];
            fileAttributes.put("Permissions", file.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION) ? "r" : "-");
            fileAttributes.put("Owner", file.getUser());
            fileAttributes.put("Size", String.valueOf(file.getSize()));
            Calendar cal = file.getTimestamp();
            Date date = cal.getTime();
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String inActiveDate = format1.format(date);
            fileAttributes.put("Last Modified Time", inActiveDate);
        }

        return fileAttributes;
    }

    private static String getFileContent(Session session, String remoteFilePath) throws JSchException, IOException {
        StringBuilder fileContent = new StringBuilder();

        String command = "cat " + remoteFilePath;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()))) {
            channel.connect();

            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            channel.disconnect();
        }

        return fileContent.toString();
    }
}